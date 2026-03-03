package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}