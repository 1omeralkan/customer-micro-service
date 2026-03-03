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
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Lütfen geçerli bir e-posta adresi giriniz.")
    @Size(max = 100, message = "Email adresi 100 karakteri geçemez.")
    private String email;

    @NotNull(message = "Doğum tarihi boş olamaz.")
    @Past(message = "Doğum tarihi bugünden ileri bir tarih olamaz.")
    private LocalDate dogumTarihi;

    @Size(min = 2, max = 50, message = "Doğum yeri geçerli bir şehir ismi olmalıdır.")
    private String dogumYeri;

    // ==========================================
    // YENİ KURUMSAL LOKASYON VE TELEFON MİMARİSİ
    // ==========================================

    private Long addressCountryId; // Kullanıcı "Türkiye" seçtiğinde bize '1' gönderecek

    private Long addressCityId;    // Kullanıcı "İstanbul" seçtiğinde bize '1' (veya ID'si neyse) gönderecek

    @Size(max = 250, message = "Açık adres alanı en fazla 250 karakter olabilir.")
    private String openAddress;    // Sadece sokak/mahalle bilgisini düz metin olarak alacağız

    private Long phoneCountryId;   // Kullanıcı "+90" seçtiğinde Türkiye'nin ID'sini gönderecek

    @Pattern(regexp = "^(5)\\d{9}$", message = "Telefon numarası 5 ile başlamalı ve 10 haneli olmalıdır.")
    private String phoneNumber;    // Sadece 5551234567 kısmını alacağız
}