package com.dev.bank.service;

import com.dev.bank.exception.customer.CustomerNotFoundException;
import com.dev.bank.model.Account;
import com.dev.bank.repository.AccountRepository;
import com.dev.bank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerEmail {
    private CustomerRepository customerRepository;
    private AccountRepository accountRepository;

    @Autowired
    public CustomerEmail(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    public String findCustomerEmail(String accountNumber){
        Account customerAccount = accountRepository.findByAccountNumber(accountNumber);
        if(customerAccount == null){
            throw new CustomerNotFoundException("cannot find customer");
        }
        return customerAccount.getCustomer().getEmail();
    }
}
