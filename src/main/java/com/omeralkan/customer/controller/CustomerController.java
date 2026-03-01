package com.omeralkan.customer.controller;

import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<CustomerResponse> saveCustomer(@Valid @RequestBody CustomerSaveRequest request) {

        CustomerResponse response = customerService.saveCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    // --- READ (ID İLE GETİRME) ---
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {

        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }
    // --- READ (TÜM KAYITLARI GETİRME) ---
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

        List<CustomerResponse> responses = customerService.getAllCustomers();
        return ResponseEntity.ok(responses);
    }

    // --- UPDATE (GÜNCELLEME) İŞLEMİ ---
    // PUT metodu, RESTful standartlarında "Güncelleme" anlamına gelir.
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerSaveRequest request) {
        // Gelen ID'yi ve yeni verileri (request) Service katmanına yolluyoruz.
        CustomerResponse response = customerService.updateCustomer(id, request);
        // İşlem başarılıysa güncel veriyi 200 OK koduyla geri dönüyoruz.
        return ResponseEntity.ok(response);
    }

    // --- DELETE (SİLME) İŞLEMİ ---
    // REST API standartlarında silme işlemi için @DeleteMapping kullanılır.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }
}