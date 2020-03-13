package com.dev.bank.service;

import com.dev.bank.config.TwillioSms;
import com.dev.bank.model.Account;
import com.dev.bank.model.TokenOtp;
import com.dev.bank.repository.TokenOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenOtpService {
    @Autowired
    private TokenOtpRepository tokenOtpRepository;

    @Scheduled(fixedDelay = 5000000, initialDelay = 5000000)
    public void deleteTokenAfterExpiry( ){
        tokenOtpRepository.deleteAll();
    }


    public Optional<TokenOtp> findByToken(String token){
    Optional<TokenOtp> tokenOtp =  tokenOtpRepository.findByToken(token);
    return tokenOtp;
    }

    public void createTokenOrOtp (Account account){
        String phoneNumber = account.getCustomer().getPhoneNumber();
        String token =  TwillioSms.sendToken(phoneNumber);
        System.out.println(token);
        // create a new token entity and persist it to database
        TokenOtp tokenOtp = new TokenOtp();
        tokenOtp.setToken(token);
        tokenOtp.setAccountNumber(account.getAccountNumber());
        tokenOtp.setTokencreated(Timestamp.valueOf(LocalDateTime.now()));
        tokenOtp.setTokenexpiry(
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(3L))
                                );
        tokenOtpRepository.save(tokenOtp);
       // deleteTokenAfterExpiry();
    }

}
