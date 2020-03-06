package com.dev.bank.repository;

import com.dev.bank.model.Account;
import com.dev.bank.model.Customer;
import com.dev.bank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RepositoryIntegrationTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void testCreateAccountsRepository(){

        Customer a = new Customer("a", "a@a.com", "a", "+234 813 705 8179", new Date(10000000));
        Account a1 = new Account("1023456789", a);
        //assertNotNull(accountService.createAccount(a1.getAccountNumber(), a1.getCustomer()));
        //assertEquals("a", accountService.createAccount(a1.getAccountNumber(), a).getCustomer().getName());
       assertNotNull(customerRepository.save(a));
       assertNotNull(accountService.createAccount(a1.getAccountNumber(), a));
       assertEquals("a", a1.getCustomer().getName());
    }

    @Test
    public void testGetAccountsRepository(){
        /**
        Date date = new Date(1000000000);
        Customer a = new Customer("a", "a@a.com", "a", date);
        Customer b = new Customer("b", "b@a.com", "b", date);
        Customer c = new Customer("c", "c@a.com", "c", date);
        Account a1 = new Account("1234567890", a);
        Account b1 = new Account("0123456789", b);
        Account c1 = new Account("1023456789", c);


        assertNotNull(accountService.createAccount(a1.getAccountNumber(), a));
         */
        //assertEquals("a", accountService.createAccount(a1.getAccountNumber(), a).getCustomer().getName());
    }
}
