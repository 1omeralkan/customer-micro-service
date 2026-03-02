package com.omeralkan.customer.repository;

import com.omeralkan.customer.entity.ErrorMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ID tipimiz String olduğu için JpaRepository<ErrorMessage, String> yazıyoruz.
@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, String> {

    // İçi tamamen boş. Standart findById(String id) metodu işimizi görecek.
}