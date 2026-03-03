package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad", nullable = false, length = 50)
    private String ad;

    @Column(name = "soyad", nullable = false, length = 50)
    private String soyad;

    @Column(name = "tc_no", nullable = false, length = 11, unique = true)
    private String tcNo;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "dogum_tarihi", nullable = false)
    private LocalDate dogumTarihi;

    @Column(name = "dogum_yeri", length = 50)
    private String dogumYeri;

    @Embedded
    private Address address;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(name = "delete_flag", nullable = false, columnDefinition = "tinyint")
    private boolean deleteFlag = false;

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
        this.deleteFlag = false;
    }

    @PreUpdate
    public void onUpdate() {
        this.updateDate = LocalDateTime.now();
        this.updateUser = "admin";
    }
}