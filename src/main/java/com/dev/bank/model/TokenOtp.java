package com.dev.bank.model;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class TokenOtp {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(nullable = true)
    private int token;

    @Column
    private String accountNumber;
    @Column
    private Instant tokencreated;
    @Column
    private Instant tokenexpiry;

    public Instant getTokenexpiry() {
        return tokenexpiry;
    }

    public void setTokenexpiry(Instant tokenexpiry) {
        this.tokenexpiry = tokenexpiry;
    }

    public TokenOtp() {
        this.tokencreated=Instant.now();
        this.tokenexpiry=Instant.now().plusMillis(300000);
    }
    public TokenOtp(String accountNumber){
        this.accountNumber= accountNumber;
    }
    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getAccountNumber() {
        return accountNumber;
    }



}
