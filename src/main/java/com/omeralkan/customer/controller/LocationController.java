package com.omeralkan.customer.controller;

import com.omeralkan.customer.dto.CityResponse;
import com.omeralkan.customer.dto.CountryResponse;
import com.omeralkan.customer.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // GET: http://localhost:8080/api/v1/locations/countries
    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        return ResponseEntity.ok(locationService.getAllCountries());
    }

    // GET: http://localhost:8080/api/v1/locations/countries/1/cities
    @GetMapping("/countries/{countryId}/cities")
    public ResponseEntity<List<CityResponse>> getCitiesByCountry(@PathVariable Long countryId) {
        return ResponseEntity.ok(locationService.getCitiesByCountry(countryId));
    }
}