package com.dev.bank.repository;

import com.dev.bank.model.TokenOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenOtpRepository extends JpaRepository<TokenOtp, Long> {
public void deleteByToken(TokenOtp tokenOtp);

public Optional<TokenOtp> findByToken(String token);

}
