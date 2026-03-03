package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SENIOR DOKUNUŞU: Lazy (Tembel) yükleme. İhtiyacımız olmadıkça ülkeyi DB'den çekip RAM'i yormayız.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "city_code", nullable = false, length = 10)
    private String cityCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

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