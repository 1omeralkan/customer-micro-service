package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PhoneNumber {

    @Column(name = "phone_country_id")
    private Long countryId;

    @Column(name = "phone_number", length = 20)
    private String number;
}