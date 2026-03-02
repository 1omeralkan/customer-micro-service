package com.omeralkan.customer.service;

import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.entity.Customer;
import com.omeralkan.customer.exception.CustomerBusinessException;
import com.omeralkan.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * MİMARİ NOT: Bu sınıf CustomerService'in "Birim" testidir.
 * Mockito ile repository katmanını taklit ederek sadece iş mantığını test eder [cite: 2026-02-24].
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // NO HARDCODED: Test verilerini sabitler üzerinden yönetiyoruz [cite: 2026-03-01].
    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Ömer";
    private static final String TEST_TCNO = "12345678901";
    private static final String TEST_EMAIL = "omer@alkan.com";

    // Hata Kodları (DB'deki karşılıkları)
    private static final String ERR_404 = "CUST-404";
    private static final String ERR_408 = "CUST-408";
    private static final String ERR_409 = "CUST-409";

    private CustomerSaveRequest request;
    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        request = new CustomerSaveRequest();
        request.setAd(TEST_NAME);
        request.setSoyad("Alkan");
        request.setTcNo(TEST_TCNO);
        request.setEmail(TEST_EMAIL);

        mockCustomer = new Customer();
        mockCustomer.setId(TEST_ID);
        mockCustomer.setAd(TEST_NAME);
        mockCustomer.setTcNo(TEST_TCNO);
        mockCustomer.setEmail(TEST_EMAIL);
    }

    // =========================================================================
    // SAVE SENARYOLARI
    // =========================================================================

    @Test
    @DisplayName("Yeni müşteri başarıyla kaydedilmelidir")
    void shouldSaveCustomerSuccessfully() {
        // GIVEN: Ön kontroller temiz
        given(customerRepository.existsByTcNo(TEST_TCNO)).willReturn(false);
        given(customerRepository.existsByEmail(TEST_EMAIL)).willReturn(false);
        given(customerRepository.save(any(Customer.class))).willReturn(mockCustomer);

        // WHEN
        CustomerResponse response = customerService.saveCustomer(request);

        // THEN
        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("TCKN zaten varsa Conflict hatası fırlatmalıdır")
    void shouldThrowConflict_WhenTcNoAlreadyExists() {
        // GIVEN: TCKN sistemde var
        given(customerRepository.existsByTcNo(TEST_TCNO)).willReturn(true);

        // WHEN & THEN
        CustomerBusinessException ex = assertThrows(CustomerBusinessException.class,
                () -> customerService.saveCustomer(request));

        assertEquals(ERR_408, ex.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        // Önemli: TCKN çakışırsa save metodu asla çağrılmamalı (Fail-Fast) [cite: 2026-02-24].
        verify(customerRepository, never()).save(any());
    }

    // =========================================================================
    // UPDATE SENARYOLARI
    // =========================================================================

    @Test
    @DisplayName("Müşteri bilgileri başarıyla güncellenmelidir")
    void shouldUpdateCustomerSuccessfully() {
        // GIVEN
        given(customerRepository.findByIdAndDeleteFlagFalse(TEST_ID)).willReturn(Optional.of(mockCustomer));
        given(customerRepository.save(any(Customer.class))).willReturn(mockCustomer);

        // WHEN
        CustomerResponse response = customerService.updateCustomer(TEST_ID, request);

        // THEN
        assertNotNull(response);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Başka bir müşteriye ait e-posta ile güncelleme yapılırsa hata fırlatmalıdır")
    void shouldThrowConflict_WhenUpdatingWithExistingEmail() {
        // GIVEN: Güncellenecek müşteri farklı bir mail kullanıyor
        mockCustomer.setEmail("farkli@mail.com");
        given(customerRepository.findByIdAndDeleteFlagFalse(TEST_ID)).willReturn(Optional.of(mockCustomer));
        given(customerRepository.existsByEmail(TEST_EMAIL)).willReturn(true);

        // WHEN & THEN
        CustomerBusinessException ex = assertThrows(CustomerBusinessException.class,
                () -> customerService.updateCustomer(TEST_ID, request));

        assertEquals(ERR_409, ex.getErrorCode());
        verify(customerRepository, never()).save(any());
    }

    // =========================================================================
    // GET & DELETE SENARYOLARI
    // =========================================================================

    @Test
    @DisplayName("ID ile müşteri bulunamazsa 404 hatası fırlatmalıdır")
    void shouldThrowNotFound_WhenCustomerDoesNotExist() {
        given(customerRepository.findByIdAndDeleteFlagFalse(TEST_ID)).willReturn(Optional.empty());

        CustomerBusinessException ex = assertThrows(CustomerBusinessException.class,
                () -> customerService.getCustomerById(TEST_ID));

        assertEquals(ERR_404, ex.getErrorCode());
    }

    @Test
    @DisplayName("Müşteri başarıyla soft-delete edilmelidir")
    void shouldSoftDeleteCustomerSuccessfully() {
        given(customerRepository.findByIdAndDeleteFlagFalse(TEST_ID)).willReturn(Optional.of(mockCustomer));

        customerService.deleteCustomer(TEST_ID);

        assertTrue(mockCustomer.isDeleteFlag());
        verify(customerRepository, times(1)).save(mockCustomer);
    }
}