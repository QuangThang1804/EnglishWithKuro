package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.EmailAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthenticationRepository extends JpaRepository<EmailAuthentication, Long> {
    Optional<EmailAuthentication> findByEmail(String email);
}
