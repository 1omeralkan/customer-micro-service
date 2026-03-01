package com.omeralkan.customer.repository;

import com.omeralkan.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 1. Sadece silinmemiş (deleteFlag = false) olan TÜM müşterileri getir.
    // SQL Karşılığı: SELECT * FROM customer WHERE delete_flag = false
    List<Customer> findAllByDeleteFlagFalse();

    // 2. Belirli bir ID'ye sahip olan VE silinmemiş olan TEK bir müşteriyi getir.
    // SQL Karşılığı: SELECT * FROM customer WHERE id = ? AND delete_flag = false
    Optional<Customer> findByIdAndDeleteFlagFalse(Long id);
}