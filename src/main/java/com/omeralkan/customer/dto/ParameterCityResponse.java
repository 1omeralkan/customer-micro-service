package com.omeralkan.customer.dto;

// Parameter servisinden gelecek şehir bilgisini karşılar
public record ParameterCityResponse(
        Long id,
        String name,
        String plateCode,
        Long countryId
) {
}
