package com.omeralkan.customer.service;

import com.omeralkan.customer.client.ParameterServiceClient;
import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.entity.Address;
import com.omeralkan.customer.entity.Customer;
import com.omeralkan.customer.entity.PhoneNumber;
import com.omeralkan.customer.exception.CustomerBusinessException;
import com.omeralkan.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ParameterServiceClient parameterServiceClient;

    private static final String ERROR_CUSTOMER_NOT_FOUND = "CUST-404";
    private static final String ERROR_TCKN_ALREADY_EXISTS = "CUST-408";
    private static final String ERROR_EMAIL_ALREADY_EXISTS = "CUST-409";
    private static final String ERROR_CITY_NOT_BELONG_TO_COUNTRY = "LOC-400";
    private static final String ERROR_EXTERNAL_SERVICE = "EXT-500";

    public CustomerResponse saveCustomer(CustomerSaveRequest request) {
        if (customerRepository.existsByTcNo(request.getTcNo())) {
            throw new CustomerBusinessException(ERROR_TCKN_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerBusinessException(ERROR_EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        Customer customer = new Customer();
        updateCustomerFields(customer, request);
        buildAndSetLocationData(customer, request);

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponse(savedCustomer);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new CustomerBusinessException(ERROR_CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));
        return mapToResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAllByDeleteFlagFalse().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CustomerResponse updateCustomer(Long id, CustomerSaveRequest request) {
        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new CustomerBusinessException(ERROR_CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!customer.getTcNo().equals(request.getTcNo()) && customerRepository.existsByTcNo(request.getTcNo())) {
            throw new CustomerBusinessException(ERROR_TCKN_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (!customer.getEmail().equals(request.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerBusinessException(ERROR_EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        updateCustomerFields(customer, request);
        buildAndSetLocationData(customer, request);

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new CustomerBusinessException(ERROR_CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));
        customer.setDeleteFlag(true);
        customerRepository.save(customer);
    }

    // Ortak alan güncellemeleri için yardımcı metod
    private void updateCustomerFields(Customer customer, CustomerSaveRequest request) {
        customer.setAd(request.getAd());
        customer.setSoyad(request.getSoyad());
        customer.setTcNo(request.getTcNo());
        customer.setEmail(request.getEmail());
        customer.setDogumTarihi(request.getDogumTarihi());
        customer.setDogumYeri(request.getDogumYeri());
    }

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setAd(customer.getAd());
        response.setSoyad(customer.getSoyad());
        response.setEmail(customer.getEmail());
        response.setTcNo(customer.getTcNo());

        if (customer.getAddress() != null) {
            response.setOpenAddress(customer.getAddress().getOpenAddress());
            Long countryId = customer.getAddress().getCountryId();
            Long cityId = customer.getAddress().getCityId();

            // FEIGN
            if (countryId != null) {
                try {
                    var country = parameterServiceClient.getCountryById(countryId);
                    response.setAddressCountryId(countryId);
                    response.setAddressCountryName(country.name());
                } catch (Exception e) {
                    log.error("Ülke ismi çekilemedi ID: {}", countryId);
                }
            }
            if (cityId != null) {
                try {

                    var city = parameterServiceClient.getCityById(cityId);
                    response.setAddressCityId(cityId);
                    response.setAddressCityName(city.name());
                } catch (Exception e) {
                    log.error("Şehir ismi çekilemedi ID: {}", cityId);
                }
            }
        }

        if (customer.getPhoneNumber() != null && customer.getPhoneNumber().getCountryId() != null) {
            response.setPhoneNumber(customer.getPhoneNumber().getNumber());
            try {
                var phoneCountry = parameterServiceClient.getCountryById(customer.getPhoneNumber().getCountryId());
                response.setPhoneCountryId(phoneCountry.id());
                response.setPhoneCode(phoneCountry.phoneCode());
            } catch (Exception e) {
                log.error("Telefon ülke bilgisi çekilemedi.");
            }
        }
        return response;
    }

    private void buildAndSetLocationData(Customer customer, CustomerSaveRequest request) {
        Address address = new Address();

        // 1. ÜLKE DOĞRULAMA (Feign)
        if (request.getAddressCountryId() != null) {
            try {
                parameterServiceClient.getCountryById(request.getAddressCountryId());
                address.setCountryId(request.getAddressCountryId());
            } catch (Exception e) {
                throw new CustomerBusinessException("Geçersiz Ülke ID: " + request.getAddressCountryId(), HttpStatus.BAD_REQUEST);
            }
        }

        // 2. ŞEHİR VE BAĞLANTI KONTROLÜ (Feign)
        if (request.getAddressCityId() != null) {
            try {
                var city = parameterServiceClient.getCityById(request.getAddressCityId());

                // İş Kuralı: Şehir seçilen ülkeye mi ait?
                if (request.getAddressCountryId() != null && !city.countryId().equals(request.getAddressCountryId())) {
                    throw new CustomerBusinessException(ERROR_CITY_NOT_BELONG_TO_COUNTRY, HttpStatus.BAD_REQUEST);
                }
                address.setCityId(request.getAddressCityId());
            } catch (CustomerBusinessException cbe) {
                throw cbe;
            } catch (Exception e) {
                throw new CustomerBusinessException("Geçersiz Şehir ID: " + request.getAddressCityId(), HttpStatus.BAD_REQUEST);
            }
        }

        address.setOpenAddress(request.getOpenAddress());
        customer.setAddress(address);

        // 3. TELEFON ÜLKE DOĞRULAMA (Feign)
        PhoneNumber phone = new PhoneNumber();
        if (request.getPhoneCountryId() != null) {
            try {
                parameterServiceClient.getCountryById(request.getPhoneCountryId());
                phone.setCountryId(request.getPhoneCountryId());
            } catch (Exception e) {
                throw new CustomerBusinessException("Geçersiz Telefon Ülke ID", HttpStatus.BAD_REQUEST);
            }
        }
        phone.setNumber(request.getPhoneNumber());
        customer.setPhoneNumber(phone);
    }
}