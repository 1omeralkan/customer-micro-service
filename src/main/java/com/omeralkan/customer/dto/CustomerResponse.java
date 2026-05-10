package com.omeralkan.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponse {



    private Long id;
    private String ad;
    private String soyad;
    private String email;
    private String tcNo;

    private Long addressCountryId;
    private String addressCountryName;

    private Long addressCityId;
    private String addressCityName;

    private String openAddress;

    private Long phoneCountryId;
    private String phoneCode;
    private String phoneNumber;
}