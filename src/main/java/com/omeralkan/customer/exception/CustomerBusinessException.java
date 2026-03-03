package com.omeralkan.customer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomerBusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public CustomerBusinessException(String errorCode, HttpStatus httpStatus) {
        super(errorCode);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}