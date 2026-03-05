package com.omeralkan.customer.service;

import com.omeralkan.customer.client.ParameterServiceClient;
import com.omeralkan.customer.dto.CustomerResponse;
import com.omeralkan.customer.dto.CustomerSaveRequest;
import com.omeralkan.customer.dto.ParameterCityResponse;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    // YENİ MİMARİ: Artık Country/City repository yok, Feign Client var!
    @Mock
    private ParameterServiceClient parameterServiceClient;

    @InjectMocks
    private CustomerService customerService;

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Ömer";
    private static final String TEST_TCNO = "12345678901";
    private static final String TEST_EMAIL = "omer@alkan.com";

    private static final String ERR_404 = "CUST-404";
    private static final String ERR_408 = "CUST-408";
    private static final String ERR_409 = "CUST-409";
    private static final String ERR_LOC_400 = "LOC-400";

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

    // SAVE SENARYOLARI
    @Test
    @DisplayName("Yeni müşteri başarıyla kaydedilmelidir")
    void shouldSaveCustomerSuccessfully() {
        given(customerRepository.existsByTcNo(TEST_TCNO)).willReturn(false);
        given(customerRepository.existsByEmail(TEST_EMAIL)).willReturn(false);
        given(customerRepository.save(any(Customer.class))).willReturn(mockCustomer);

        CustomerResponse response = customerService.saveCustomer(request);

        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("TCKN zaten varsa Conflict hatası fırlatmalıdır")
    void shouldThrowConflict_WhenTcNoAlreadyExists() {
        given(customerRepository.existsByTcNo(TEST_TCNO)).willReturn(true);

        CustomerBusinessException ex = assertThrows(CustomerBusinessException.class,
                () -> customerService.saveCustomer(request));

        // Senin özel exception sınıfına göre metodları korudum (getErrorCode vs getMessage)
        assertEquals(ERR_408, ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Şehir seçilen ülkeye ait değilse Bad Request (LOC-400) fırlatmalıdır")
    void shouldThrowBadRequest_WhenCityDoesNotBelongToCountry() {
        final Long requestCountryId = 1L; // Kullanıcı Türkiye'yi seçti
        final Long requestCityId = 5L;    // Kullanıcı Texas'ı seçti
        final Long realCountryIdOfCity = 2L; // Texas ABD'ye ait

        request.setAddressCountryId(requestCountryId);
        request.setAddressCityId(requestCityId);

        given(customerRepository.existsByTcNo(TEST_TCNO)).willReturn(false);
        given(customerRepository.existsByEmail(TEST_EMAIL)).willReturn(false);

        // YENİ MİMARİ: Feign Client'tan gelen DTO'yu mockluyoruz!
        ParameterCityResponse mockCityResponse = mock(ParameterCityResponse.class);
        given(mockCityResponse.countryId()).willReturn(realCountryIdOfCity);

        given(parameterServiceClient.getCityById(requestCityId)).willReturn(mockCityResponse);

        CustomerBusinessException ex = assertThrows(CustomerBusinessException.class,
                () -> customerService.saveCustomer(request));

        assertEquals(ERR_LOC_400, ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // UPDATE SENARYOLARI
    @Test
    @DisplayName("Müşteri bilgileri başarıyla güncellenmelidir")
    void shouldUpdateCustomerSuccessfully() {
        given(customerRepository.findByIdAndDeleteFlagFalse(TEST_ID)).willReturn(Optional.of(mockCustomer));
        given(customerRepository.save(any(Customer.class))).willReturn(mockCustomer);

        CustomerResponse response = customerService.updateCustomer(TEST_ID, request);

        assertNotNull(response);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Başka bir müşteriye ait e-posta ile güncelleme yapılırsa hata fırlatmalıdır")
    void shouldThrowConflict_WhenUpdatingWithExistingEmail() {
        mockCustomer.setEmail("farkli@mail.com");
        given(customerRepository.findByIdAndDeleteFlagFalse(TEST_ID)).willReturn(Optional.of(mockCustomer));
        given(customerRepository.existsByEmail(TEST_EMAIL)).willReturn(true);

        CustomerBusinessException ex = assertThrows(CustomerBusinessException.class,
                () -> customerService.updateCustomer(TEST_ID, request));

        assertEquals(ERR_409, ex.getMessage());
        verify(customerRepository, never()).save(any());
    }

    // GET & DELETE SENARYOLARI
    @Test
    @DisplayName("ID ile müşteri bulunamazsa 404 hatası fırlatmalıdır")
    void shouldThrowNotFound_WhenCustomerDoesNotExist() {
        given(customerRepository.findByIdAndDeleteFlagFalse(TEST_ID)).willReturn(Optional.empty());

        CustomerBusinessException ex = assertThrows(CustomerBusinessException.class,
                () -> customerService.getCustomerById(TEST_ID));

        assertEquals(ERR_404, ex.getMessage());
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