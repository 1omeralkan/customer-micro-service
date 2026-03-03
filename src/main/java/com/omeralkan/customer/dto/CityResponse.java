package com.omeralkan.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityResponse {
    private Long id;
    private String cityCode;
    private String name;
}