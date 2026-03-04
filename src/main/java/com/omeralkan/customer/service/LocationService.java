package com.omeralkan.customer.service;

import com.omeralkan.customer.dto.CityResponse;
import com.omeralkan.customer.dto.CountryResponse;
import com.omeralkan.customer.entity.City;
import com.omeralkan.customer.entity.Country;
import com.omeralkan.customer.repository.CityRepository;
import com.omeralkan.customer.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    //Veritabanından bir kez okunur, "countries" adıyla RAM'e yazılır.
    @Cacheable(value = "countries")
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(this::mapToCountryResponse)
                .toList();
    }

    @Cacheable(value = "cities", key = "#countryId")
    public List<CityResponse> getCitiesByCountry(Long countryId) {
        return cityRepository.findAllByCountryId(countryId).stream()
                .map(this::mapToCityResponse)
                .toList();
    }

    private CountryResponse mapToCountryResponse(Country country) {
        CountryResponse response = new CountryResponse();
        response.setId(country.getId());
        response.setIsoCode(country.getIsoCode());
        response.setName(country.getName());
        response.setPhoneCode(country.getPhoneCode());
        return response;
    }

    private CityResponse mapToCityResponse(City city) {
        CityResponse response = new CityResponse();
        response.setId(city.getId());
        response.setCityCode(city.getCityCode());
        response.setName(city.getName());
        return response;
    }
}