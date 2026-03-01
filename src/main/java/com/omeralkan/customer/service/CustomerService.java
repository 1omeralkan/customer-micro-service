package com.omeralkan.customer.service;

import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.entity.Customer;
import com.omeralkan.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private static final String CUSTOMER_NOT_FOUND_MESSAGE = "Müşteri bulunamadı veya silinmiş! ID: ";

    public CustomerResponse saveCustomer(CustomerSaveRequest request) {

        // 2. Request DTO -> Entity Çevirimi
        Customer customer = new Customer();
        customer.setAd(request.getAd());
        customer.setSoyad(request.getSoyad());
        customer.setTcNo(request.getTcNo());
        customer.setEmail(request.getEmail());
        customer.setDogumTarihi(request.getDogumTarihi());
        customer.setDogumYeri(request.getDogumYeri());
        customer.setAdres(request.getAdres());
        customer.setTelNo(request.getTelNo());

        // 3. Kayıt İşlemi (Entity veritabanına gider, ID alıp geri gelir)
        Customer savedCustomer = customerRepository.save(customer);

        // 4. Entity -> Response DTO Çevirimi
        // Veritabanından gelen ID'li nesneyi, dışarı çıkacak DTO'ya çeviriyoruz.
        CustomerResponse response = new CustomerResponse();
        response.setId(savedCustomer.getId());
        response.setAd(savedCustomer.getAd());
        response.setSoyad(savedCustomer.getSoyad());
        response.setEmail(savedCustomer.getEmail());
        response.setTcNo(savedCustomer.getTcNo());

        return response;
    }

    public CustomerResponse getCustomerById(Long id) {

        // Repository'den sadece silinmemiş kaydı getiriyoruz; yoksa RuntimeException fırlatıyoruz
         Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                 .orElseThrow(() -> new RuntimeException(CUSTOMER_NOT_FOUND_MESSAGE + id));

        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setAd(customer.getAd());
        response.setSoyad(customer.getSoyad());
        response.setEmail(customer.getEmail());
        response.setTcNo(customer.getTcNo());

        return response;
    }

    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAllByDeleteFlagFalse();
        return customers.stream().map(customer -> {
            CustomerResponse response = new CustomerResponse();
            response.setId(customer.getId());
            response.setAd(customer.getAd());
            response.setSoyad(customer.getSoyad());
            response.setEmail(customer.getEmail());
            response.setTcNo(customer.getTcNo());
            return response;
        }).toList();
    }

    // --- UPDATE (GÜNCELLEME) İŞLEMİ ---

    public CustomerResponse updateCustomer(Long id, CustomerSaveRequest request) {

        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new RuntimeException(CUSTOMER_NOT_FOUND_MESSAGE + id));

        customer.setAd(request.getAd());
        customer.setSoyad(request.getSoyad());
        customer.setTcNo(request.getTcNo());
        customer.setEmail(request.getEmail());
        customer.setDogumTarihi(request.getDogumTarihi());
        customer.setDogumYeri(request.getDogumYeri());
        customer.setAdres(request.getAdres());
        customer.setTelNo(request.getTelNo());


        Customer updatedCustomer = customerRepository.save(customer);

        CustomerResponse response = new CustomerResponse();
        response.setId(updatedCustomer.getId());
        response.setAd(updatedCustomer.getAd());
        response.setSoyad(updatedCustomer.getSoyad());
        response.setEmail(updatedCustomer.getEmail());
        response.setTcNo(updatedCustomer.getTcNo());

        return response;
    }

    // --- DELETE (SOFT DELETE / YUMUŞAK SİLME) İŞLEMİ ---

    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new RuntimeException(CUSTOMER_NOT_FOUND_MESSAGE + id));

        customer.setDeleteFlag(true);

        customerRepository.save(customer);
    }

}