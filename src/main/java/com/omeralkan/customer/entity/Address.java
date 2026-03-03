package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Bu bir tablo (Entity) değildir. Başka bir tablonun içine gömülecek bir parçadır!
@Embeddable
@Getter
@Setter
public class Address {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_city_id")
    private City city;

    @Column(name = "open_address", length = 255)
    private String openAddress;
}