package com.omeralkan.customer.service;

import com.omeralkan.customer.repository.ErrorMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorMessageService {

    private final ErrorMessageRepository errorMessageRepository;

    private static final String DEFAULT_ERROR_MESSAGE = "Beklenmeyen bir sistem hatası oluştu.";

    // SENIOR DOKUNUŞU: Cache key artık dil parametresini de içeriyor!
    @Cacheable(value = "errorMessages", key = "#errorCode + '_' + #language")
    public String getMessage(String errorCode, String language) {

        return errorMessageRepository.findByErrorCodeAndLanguage(errorCode, language)
                .map(errorMessage -> errorMessage.getMessage())
                .orElse(DEFAULT_ERROR_MESSAGE + " (Code: " + errorCode + ")");
    }
}