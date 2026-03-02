package com.omeralkan.customer.service;

import com.omeralkan.customer.repository.ErrorMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorMessageService {

    private final ErrorMessageRepository errorMessageRepository;

    // CLEAN CODE KURALI: Kodun ortasına string gömülmez. Sabit (Constant) olarak tanımlanır.
    private static final String DEFAULT_ERROR_MESSAGE = "Beklenmeyen bir sistem hatası oluştu.";

    // Gelen errorCode'u (ID'yi) önce RAM'de (Cache) arar. Bulamazsa DB'ye gider.
    @Cacheable(value = "errorMessages", key = "#errorCode")
    public String getMessage(String errorCode) {

        // Kaan abinin kuralı: Sadece ID ile bul!
        return errorMessageRepository.findById(errorCode)
                .map(errorMessage -> errorMessage.getMessage())
                .orElse(DEFAULT_ERROR_MESSAGE + " (Kod: " + errorCode + ")");
    }
}