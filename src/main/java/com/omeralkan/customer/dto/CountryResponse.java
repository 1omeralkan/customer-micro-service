package com.omeralkan.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryResponse {
    private Long id;
    private String isoCode;
    private String name;
    private String phoneCode;
}