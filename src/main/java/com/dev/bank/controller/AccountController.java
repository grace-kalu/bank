package com.dev.bank.controller;

import com.dev.bank.model.Account;
import com.dev.bank.model.Customer;
import com.dev.bank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/accounts")
    public Page<Account> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @PostMapping("/accounts")
    public Account createAccount(@Valid @RequestBody Account account) {
        return accountRepository.save(account);
    }

    @PutMapping("/payments")
   public ResponseEntity<?> depositOrMakePayment(Account debitId, Account creditId, double payment){

        debitId.setBalance(debitId.getBalance() - payment);
       creditId.setBalance(creditId.getBalance() + payment);

        accountRepository.save(creditId);
        accountRepository.save(debitId);
        return ResponseEntity.ok().build();
   }
   @PutMapping("/withdrawals")
   public ResponseEntity<?> withdraw(Account accountToBeDebited, Account accountToBeCredited, double payment){


       accountToBeDebited.setBalance(accountToBeDebited.getBalance() - payment);
       accountToBeCredited.setBalance(accountToBeCredited.getBalance() + payment);

       accountRepository.save(accountToBeCredited);
       accountRepository.save(accountToBeDebited);
       return ResponseEntity.ok().build();
   }
}
