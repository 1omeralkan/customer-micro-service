package com.omeralkan.customer.service;

import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.entity.Address;
import com.omeralkan.customer.entity.Customer;
import com.omeralkan.customer.entity.PhoneNumber;
import com.omeralkan.customer.exception.CustomerBusinessException;
import com.omeralkan.customer.repository.CityRepository;
import com.omeralkan.customer.repository.CountryRepository;
import com.omeralkan.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.omeralkan.customer.entity.City;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository; // YENİ: Ülkeleri DB'den çekmek için
    private final CityRepository cityRepository;       // YENİ: Şehirleri DB'den çekmek için

    // MİMARİ KARAR: Hata kodlarını merkezi ve değişmez (constant) yaptık [cite: 2026-03-01].
    private static final String ERROR_CUSTOMER_NOT_FOUND = "CUST-404";
    private static final String ERROR_TCKN_ALREADY_EXISTS = "CUST-408";
    private static final String ERROR_EMAIL_ALREADY_EXISTS = "CUST-409";
    private static final String ERROR_CITY_NOT_BELONG_TO_COUNTRY = "LOC-400";

    public CustomerResponse saveCustomer(CustomerSaveRequest request) {

        if (customerRepository.existsByTcNo(request.getTcNo())) {
            throw new CustomerBusinessException(ERROR_TCKN_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerBusinessException(ERROR_EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        Customer customer = new Customer();
        customer.setAd(request.getAd());
        customer.setSoyad(request.getSoyad());
        customer.setTcNo(request.getTcNo());
        customer.setEmail(request.getEmail());
        customer.setDogumTarihi(request.getDogumTarihi());
        customer.setDogumYeri(request.getDogumYeri());

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

        customer.setAd(request.getAd());
        customer.setSoyad(request.getSoyad());
        customer.setTcNo(request.getTcNo());
        customer.setEmail(request.getEmail());
        customer.setDogumTarihi(request.getDogumTarihi());
        customer.setDogumYeri(request.getDogumYeri());

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

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setAd(customer.getAd());
        response.setSoyad(customer.getSoyad());
        response.setEmail(customer.getEmail());
        response.setTcNo(customer.getTcNo());

        if (customer.getAddress() != null) {
            response.setOpenAddress(customer.getAddress().getOpenAddress());

            if (customer.getAddress().getCountry() != null) {
                response.setAddressCountryId(customer.getAddress().getCountry().getId());
                response.setAddressCountryName(customer.getAddress().getCountry().getName());
            }
            if (customer.getAddress().getCity() != null) {
                response.setAddressCityId(customer.getAddress().getCity().getId());
                response.setAddressCityName(customer.getAddress().getCity().getName());
            }
        }

        if (customer.getPhoneNumber() != null) {
            response.setPhoneNumber(customer.getPhoneNumber().getNumber());

            if (customer.getPhoneNumber().getCountry() != null) {
                response.setPhoneCountryId(customer.getPhoneNumber().getCountry().getId());
                response.setPhoneCode(customer.getPhoneNumber().getCountry().getPhoneCode());
            }
        }

        return response;
    }

    private void buildAndSetLocationData(Customer customer, CustomerSaveRequest request) {

        Address address = new Address();

        if (request.getAddressCountryId() != null) {
            address.setCountry(countryRepository.getReferenceById(request.getAddressCountryId()));
        }

        if (request.getAddressCityId() != null) {

            City city = cityRepository.findById(request.getAddressCityId())
                    .orElseThrow(() -> new CustomerBusinessException(ERROR_CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

            if (request.getAddressCountryId() != null && !city.getCountry().getId().equals(request.getAddressCountryId())) {
                throw new CustomerBusinessException(ERROR_CITY_NOT_BELONG_TO_COUNTRY, HttpStatus.BAD_REQUEST);
            }

            address.setCity(city);
        }
        address.setOpenAddress(request.getOpenAddress());
        customer.setAddress(address);

        PhoneNumber phone = new PhoneNumber();
        if (request.getPhoneCountryId() != null) {
            phone.setCountry(countryRepository.getReferenceById(request.getPhoneCountryId()));
        }
        phone.setNumber(request.getPhoneNumber());
        customer.setPhoneNumber(phone);
    }
}