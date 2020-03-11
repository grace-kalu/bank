package com.dev.bank.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class TokenOtp {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(unique = true)
    private String token;
    @Column
    private String accountNumber;
    @Column
    private Timestamp tokencreated;
    @Column
    private Timestamp tokenexpiry;

    public Timestamp getTokenexpiry() {
        return tokenexpiry;
    }

    public void setTokenexpiry(Timestamp tokenexpiry) {
        this.tokenexpiry = tokenexpiry;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Timestamp getTokencreated() {
        return tokencreated;
    }

    public void setTokencreated(Timestamp tokencreated) {
        this.tokencreated = tokencreated;
    }

    public TokenOtp() {

    }
    public TokenOtp(String accountNumber){
        this.accountNumber= accountNumber;
        this.tokencreated= Timestamp.valueOf( LocalDateTime.now());
        this.tokenexpiry=Timestamp.valueOf(LocalDateTime.now().plusMinutes(3L));
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccountNumber() {
        return accountNumber;
    }



}
