package com.omeralkan.customer.repository;

import com.omeralkan.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findAllByDeleteFlagFalse();

    Optional<Customer> findByIdAndDeleteFlagFalse(Long id);

    boolean existsByTcNo(String tcNo);


    boolean existsByEmail(String email);
}