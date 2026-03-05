package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {

    @Column(name = "address_country_id")
    private Long countryId;

    @Column(name = "address_city_id")
    private Long cityId;

    @Column(name = "open_address", length = 255)
    private String openAddress;
}