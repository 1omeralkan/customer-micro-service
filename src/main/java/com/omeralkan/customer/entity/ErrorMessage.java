package com.omeralkan.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "error_messages")
@Data
public class ErrorMessage {

    // Doğal Anahtar (Natural Key): Hata kodunun kendisi artık ID'miz.
    @Id
    @Column(name = "error_code", nullable = false, length = 50)
    private String errorCode;

    // Ekrana basılacak olan hata metni (Şimdilik sadece Türkçe)
    @Column(name = "message", nullable = false, length = 255)
    private String message;
}