package com.dev.bank.exception.customer;

public class CustomerNotFoundExceptionResponse {
    private String accountNumber;

    public CustomerNotFoundExceptionResponse(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
