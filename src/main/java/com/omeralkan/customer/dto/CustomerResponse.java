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
}