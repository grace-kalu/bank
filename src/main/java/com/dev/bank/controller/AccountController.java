package com.dev.bank.controller;

import com.dev.bank.model.Account;
import com.dev.bank.model.Customer;
import com.dev.bank.repository.AccountRepository;
import com.dev.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public Page<Account> getAllAccounts(@PathVariable int page, @PathVariable int size) {
        return accountService.getAccountsList(page, size);
    }

    @PostMapping("/accounts")
    public void createAccount(@Valid @RequestBody String accountNumber,
                                 @Valid @RequestBody Customer customer) {
        accountService.createAccount(accountNumber, customer);
    }

    @PutMapping("/payments")
   public ResponseEntity<?> makePayment(@Valid @RequestBody String accountNumber,
                                        @Valid @RequestBody Double amount){

        Account account = accountService.findAccountByAccountNumber(accountNumber);
        if (account == null){
            return ResponseEntity.
                    status(HttpStatus.NOT_FOUND).
                    body("invalid account number");
        }
        if(amount <= 0 ){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST).
                    body("transaction can't be processed");
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body("transaction completed");
   }

   @PutMapping("/withdrawals")
   public ResponseEntity<?> withdraw(@Valid @RequestBody String accountNumber,
                                     @Valid @RequestBody double amount){
       Account account = accountService.findAccountByAccountNumber(accountNumber);

       if (account == null){
           return ResponseEntity.
                   status(HttpStatus.NOT_FOUND).
                   body("invalid account number");
       }
       if(account.getBalance() < amount ){
           return ResponseEntity.
                   status(HttpStatus.BAD_REQUEST).
                   body("transaction can't be processed");
       }

       account.setBalance(account.getBalance() - amount);
       accountRepository.save(account);

       return ResponseEntity.status(HttpStatus.OK).body("transaction completed");
   }
}
