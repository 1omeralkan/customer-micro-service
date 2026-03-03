package com.omeralkan.customer.repository;

import com.omeralkan.customer.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}