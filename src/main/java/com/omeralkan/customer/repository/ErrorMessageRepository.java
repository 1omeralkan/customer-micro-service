package com.omeralkan.customer.repository;

import com.omeralkan.customer.entity.ErrorMessage;
import com.omeralkan.customer.entity.ErrorMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, ErrorMessageId> {

    // Clean Code: Servisten kolayca çağırmak için özel metot
    Optional<ErrorMessage> findByErrorCodeAndLanguage(String errorCode, String language);
}