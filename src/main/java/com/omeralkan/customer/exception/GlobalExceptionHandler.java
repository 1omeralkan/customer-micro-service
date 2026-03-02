package com.omeralkan.customer.exception;

import com.omeralkan.customer.service.ErrorMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMessageService errorMessageService;

    private static final String VALIDATION_ERROR_CODE = "SYS-400";
    private static final String SYSTEM_ERROR_CODE = "SYS-500";

    // =========================================================================
    // YAKALAYICI 1: BİZİM İŞ KURALLARIMIZ (CustomerBusinessException)
    // =========================================================================
    @ExceptionHandler(CustomerBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(CustomerBusinessException ex) {

        String localizedMessage = errorMessageService.getMessage(ex.getErrorCode());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(ex.getHttpStatus().value())
                .businessErrorCode(ex.getErrorCode())
                .message(localizedMessage)
                // Not: .details() setlenmediği için null kalır ve @JsonInclude sayesinde JSON'da gözükmez.
                .build();

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    // =========================================================================
    // YAKALAYICI 2: DTO VALIDASYON HATALARI (@ValidTcNo, @Email vb.)
    // =========================================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        // [ZIRH DELİCİ]: get(0) devri bitti knk. Tüm hataları stream ile topluyoruz.
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        // Ana mesajımızı yine DB'den (SYS-400) çekiyoruz.
        String mainMessage = errorMessageService.getMessage(VALIDATION_ERROR_CODE);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .businessErrorCode(VALIDATION_ERROR_CODE)
                .message(mainMessage)
                .details(details) // İşte testçinin (QA) bayılacağı o liste!
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // =========================================================================
    // YAKALAYICI 3: BEKLENMEYEN SİSTEM ÇÖKMELERİ
    // =========================================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        log.error("Sistemde beklenmeyen bir çökme meydana geldi: ", ex);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .businessErrorCode(SYSTEM_ERROR_CODE)
                .message(errorMessageService.getMessage(SYSTEM_ERROR_CODE))
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}