package com.omeralkan.customer.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int httpStatus;

    private String businessErrorCode;

    private String message;


    private List<String> details;

    public ErrorResponse(String businessErrorCode, String message, int httpStatus) {
        this.businessErrorCode = businessErrorCode;
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
    }
}