package com.dev.bank.service;

import com.dev.bank.model.TokenOtp;
import com.dev.bank.repository.TokenOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenOtpService {
    @Autowired
    private TokenOtpRepository tokenOtpRepository;
/*
    @Scheduled(fixedDelay = 300000)
    public void deleteTokenAfterExpiry(TokenOtp tokenOtp){
        tokenOtpRepository.deleteByToken(tokenOtp);
    }

 */
    public TokenOtp findByToken(int token){
    TokenOtp tokenOtp =  tokenOtpRepository.findByToken(token);
    return tokenOtp;
    }

}
