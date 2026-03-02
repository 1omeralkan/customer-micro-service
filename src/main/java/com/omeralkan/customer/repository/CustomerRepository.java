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

    // --- YENİ EKLENEN "ZIRH" METOTLARI ---

    // 1. Bu TCKN veritabanında (silinmiş olsa dahi) mevcut mu?
    // SQL: SELECT COUNT(*) > 0 FROM customer WHERE tc_no = ?
    boolean existsByTcNo(String tcNo);

    // 2. Bu e-posta veritabanında (silinmiş olsa dahi) mevcut mu?
    // SQL: SELECT COUNT(*) > 0 FROM customer WHERE email = ?
    boolean existsByEmail(String email);
}