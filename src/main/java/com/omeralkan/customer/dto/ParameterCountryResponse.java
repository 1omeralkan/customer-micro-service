package com.omeralkan.customer.dto;

// Parameter servisinden gelecek ülke bilgisini karşılar
public record ParameterCountryResponse(
        Long id,
        String name,
        String isoCode,
        String phoneCode
) {
}