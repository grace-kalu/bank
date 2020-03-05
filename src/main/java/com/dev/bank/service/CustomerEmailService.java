package com.dev.bank.service;

import java.util.Random;

import com.dev.bank.exception.customer.CustomerNotFoundException;
import com.dev.bank.model.Account;
import com.dev.bank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerEmailService {
    private AccountRepository accountRepository;

    @Autowired
    public CustomerEmailService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String findCustomerEmail(String accountNumber){
        Account customerAccount = accountRepository.findByAccountNumber(accountNumber);
        if(customerAccount == null){
            throw new CustomerNotFoundException("cannot find customer");
        }
        return customerAccount.getCustomer().getEmail();
    }

    public String findCustomerPhone(String accountNumber){
        Account customerAccount = accountRepository.findByAccountNumber(accountNumber);
        if(customerAccount == null){
            throw new CustomerNotFoundException("cannot find customer");
        }
        return customerAccount.getCustomer().getPhone();
    }

    public String GenerateCustomerToken(String accountNumber){
        Account customerAccount = accountRepository.findByAccountNumber(accountNumber);
        if(customerAccount == null){
            throw new CustomerNotFoundException("cannot find customer");
        }

        Random rand = new Random();
        int randomNumber = rand.nextInt(1000000);
        return String.valueOf(randomNumber);
    }
}
