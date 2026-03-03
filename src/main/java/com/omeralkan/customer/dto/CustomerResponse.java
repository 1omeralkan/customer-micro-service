package com.omeralkan.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponse {

    // Dışarıya sadece bu bilgileri dönmek istiyoruz.
    // Mesela 'deleteFlag', 'updateUser' gibi teknik detayları GİZLEDİK.

    private Long id;
    private String ad;
    private String soyad;
    private String email;
    private String tcNo;

    private Long addressCountryId;
    private String addressCountryName; // Front-end ekrana "Türkiye" yazabilsin diye

    private Long addressCityId;
    private String addressCityName;    // Front-end ekrana "İstanbul" yazabilsin diye

    private String openAddress;

    private Long phoneCountryId;
    private String phoneCode;          // Örn: "+90"
    private String phoneNumber;        // Örn: "5551234567"
}