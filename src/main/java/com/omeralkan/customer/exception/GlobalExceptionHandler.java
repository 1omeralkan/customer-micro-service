package com.omeralkan.customer.exception;

import com.omeralkan.customer.service.ErrorMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder; // SENIOR DOKUNUŞU EKLENDİ
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private String getCurrentLanguage() {

        return LocaleContextHolder.getLocale().getLanguage();
    }

    @ExceptionHandler(CustomerBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(CustomerBusinessException ex) {

        String localizedMessage = errorMessageService.getMessage(ex.getErrorCode(), getCurrentLanguage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(ex.getHttpStatus().value())
                .businessErrorCode(ex.getErrorCode())
                .message(localizedMessage)
                .build();

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        String mainMessage = errorMessageService.getMessage(VALIDATION_ERROR_CODE, getCurrentLanguage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .businessErrorCode(VALIDATION_ERROR_CODE)
                .message(mainMessage)
                .details(details)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        log.error("Sistemde beklenmeyen bir çökme meydana geldi: ", ex);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .businessErrorCode(SYSTEM_ERROR_CODE)
                .message(errorMessageService.getMessage(SYSTEM_ERROR_CODE, getCurrentLanguage()))
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}