package com.omeralkan.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageId implements Serializable {
    private String errorCode;
    private String language;
}