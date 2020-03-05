package com.dev.bank.repository;

import com.dev.bank.model.TokenOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenOtpRepository extends JpaRepository<TokenOtp, Long> {
public void deleteByToken(TokenOtp tokenOtp);

public TokenOtp findByToken(int token);

}
