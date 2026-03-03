package com.omeralkan.customer.repository;

import com.omeralkan.customer.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByCountryId(Long countryId);
}