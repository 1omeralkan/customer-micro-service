package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "create_user", nullable = false)
    private String createUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_user")
    private String updateUser;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.createUser = "admin";
    }

    @PreUpdate
    public void onUpdate() {
        this.updateDate = LocalDateTime.now();
        this.updateUser = "admin";
    }
}