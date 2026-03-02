package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "error_messages")
@Data
@IdClass(ErrorMessageId.class) // JPA'ya ID sınıfımızı tanıtıyoruz
public class ErrorMessage {

    @Id
    @Column(name = "error_code", nullable = false, length = 50)
    private String errorCode;

    @Id
    @Column(name = "language", nullable = false, length = 5)
    private String language;

    @Column(name = "message", nullable = false, length = 255)
    private String message;
}