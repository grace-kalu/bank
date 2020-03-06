
package com.dev.bank.service;

import com.dev.bank.config.TwillioSms;
import com.dev.bank.model.Account;
import com.dev.bank.model.Customer;
import com.dev.bank.model.TokenOtp;
import com.dev.bank.repository.AccountRepository;
import com.dev.bank.repository.TokenOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TokenOtpRepository tokenOtpRepository;

    @Autowired
    private TokenOtpService tokenOtpService;

public Account findAccountByAccountNumber(String accountNumber){
    return accountRepository.findByAccountNumber(accountNumber);
}

public Account createAccount(String accountNumber, Customer customer){
    Account newAccount = new Account();

    newAccount.setAccountNumber(accountNumber);
    newAccount.setCustomer(customer);
    newAccount.setBalance(0.0);

    TokenOtp tokenOtp = new TokenOtp(newAccount.getAccountNumber());

   int token= TwillioSms.sendToken(customer.getPhoneNumber());
   tokenOtp.setToken(token);
//    check and confirm if sms delivered
    tokenOtp.setTokenexpiry(Instant.now().plusMillis(180000));

    tokenOtpRepository.save(tokenOtp);
    //tokenOtpService.deleteTokenAfterExpiry(tokenOtp);

    accountRepository.save(newAccount);
    return newAccount;
}

public Page<Account> getAccountsList(int page, int size) {

    PageRequest pageReq
            = PageRequest.of(page, size);
    Page<Account> accounts = accountRepository.findAll(pageReq);
        //return accounts.getContent();
        return accounts;
    }

/*public Account findAccountByCardPAN(String PAN){
    Account customerAccount = accountRepository.findAccountByCard(PAN);
    return customerAccount;
}*/


}

