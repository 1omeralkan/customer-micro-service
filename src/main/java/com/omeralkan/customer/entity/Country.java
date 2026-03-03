package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iso_code", nullable = false, length = 5, unique = true)
    private String isoCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone_code", nullable = false, length = 10)
    private String phoneCode;

    // =======================================================
    // AUDIT (DENETİM) ALANLARI
    // =======================================================

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