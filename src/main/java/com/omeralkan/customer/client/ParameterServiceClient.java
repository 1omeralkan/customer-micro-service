package com.omeralkan.customer.client;

import com.omeralkan.customer.dto.ParameterCityResponse;
import com.omeralkan.customer.dto.ParameterCountryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "parameter-service", url = "http://localhost:8081/api/v1")
public interface ParameterServiceClient {

    //Plakaya göre şehri getir
    @GetMapping("/cities/plate/{plateCode}")
    ParameterCityResponse getCityByPlateCode(@PathVariable("plateCode") String plateCode);

    //Ülke ID'sine göre ülkeyi getir
    @GetMapping("/countries/{id}")
    ParameterCountryResponse getCountryById(@PathVariable("id") Long id);

    @GetMapping("/cities/{id}")
    ParameterCityResponse getCityById(@PathVariable("id") Long id);
}