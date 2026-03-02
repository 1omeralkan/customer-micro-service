package com.omeralkan.customer.service;

import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.entity.Customer;
import com.omeralkan.customer.exception.CustomerBusinessException;
import com.omeralkan.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // MİMARİ KARAR (NO HARDCODED): Hata kodlarımızı merkezi sabitler olarak tanımlıyoruz [cite: 2026-03-01].
    private static final String ERROR_CUSTOMER_NOT_FOUND = "CUST-404";
    private static final String ERROR_TCKN_ALREADY_EXISTS = "CUST-408";
    private static final String ERROR_EMAIL_ALREADY_EXISTS = "CUST-409";

    public CustomerResponse saveCustomer(CustomerSaveRequest request) {

        // [ZIRH KATMANI]: Veritabanı hatası fırlatmadan önce iş kuralı denetimi yapıyoruz.
        // 1. TCKN Benzersizlik Kontrolü
        if (customerRepository.existsByTcNo(request.getTcNo())) {
            throw new CustomerBusinessException(ERROR_TCKN_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        // 2. Email Benzersizlik Kontrolü
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
        customer.setAdres(request.getAdres());
        customer.setTelNo(request.getTelNo());

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new CustomerBusinessException(ERROR_CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

        return mapToResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAllByDeleteFlagFalse();
        return customers.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CustomerResponse updateCustomer(Long id, CustomerSaveRequest request) {
        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new CustomerBusinessException(ERROR_CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

        // [KRİTİK]: TCKN veya Email değişiyorsa, yeni değerlerin başkasında olup olmadığını denetliyoruz.
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
        customer.setAdres(request.getAdres());
        customer.setTelNo(request.getTelNo());

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new CustomerBusinessException(ERROR_CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

        customer.setDeleteFlag(true);
        customerRepository.save(customer);
    }

    // DRY PRENSİBİ: Tekrarlayan mapping (Entity -> Response) kodunu merkezi bir metoda aldık [cite: 2026-02-23].
    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setAd(customer.getAd());
        response.setSoyad(customer.getSoyad());
        response.setEmail(customer.getEmail());
        response.setTcNo(customer.getTcNo());
        return response;
    }
}