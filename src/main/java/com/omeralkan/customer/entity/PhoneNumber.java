package com.omeralkan.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PhoneNumber {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_country_id")
    private Country country;

    @Column(name = "phone_number", length = 20)
    private String number;
}