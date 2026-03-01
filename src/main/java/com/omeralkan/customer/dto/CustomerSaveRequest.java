package com.omeralkan.customer.dto;

import com.omeralkan.customer.validation.ValidTcNo;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerSaveRequest {

    @NotBlank(message = "Ad alanı boş geçilemez.")
    @Size(min = 2, max = 50, message = "Ad en az 2, en fazla 50 karakter olabilir.")
    private String ad;

    @NotBlank(message = "Soyad alanı boş geçilemez.")
    @Size(min = 2, max = 50, message = "Soyad en az 2, en fazla 50 karakter olabilir.")
    private String soyad;

    @NotBlank(message = "TC Kimlik Numarası boş geçilemez.")
    @Size(min = 11, max = 11, message = "TC Kimlik Numarası tam 11 haneli olmalıdır.")
    @ValidTcNo
    private String tcNo;

    @NotBlank(message = "Email alanı boş geçilemez.")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Lütfen geçerli bir e-posta adresi giriniz. (Örn: ornek@domain.com)")
    @Size(max = 100, message = "Email adresi 100 karakteri geçemez.")
    private String email;

    @NotNull(message = "Doğum tarihi boş olamaz.")
    @Past(message = "Doğum tarihi bugünden ileri bir tarih olamaz.")
    private LocalDate dogumTarihi;

    // Eğer veri gelirse kurallara uymak zorunda, gelmezse (null) sorun yok.

    @Size(min = 2, max = 50, message = "Doğum yeri geçerli bir şehir ismi olmalıdır.")
    private String dogumYeri;

    @Size(max = 250, message = "Adres alanı en fazla 250 karakter olabilir.")
    private String adres;

    // Telefon gelirse formatı doğru olmalı, gelmezse (null) hata vermez.
    @Pattern(regexp = "^(5)\\d{9}$", message = "Telefon numarası 5 ile başlamalı ve 10 haneli olmalıdır.")
    private String telNo;
}