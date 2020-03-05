//package com.dev.bank.controller;
//
//import com.dev.bank.model.Account;
//import com.dev.bank.service.AccountService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/account")
//public class AccountController {
//    private final AccountService accountService;
//
//    public AccountController(AccountService accountService) {
//        this.accountService = accountService;
//    }
//
//    @GetMapping("/card")
//    public ResponseEntity<Account> getAccountByCard(String pan) {
//        Account response = accountService.getAccountByCard(pan);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/accountNumber")
//    public ResponseEntity<Account> getAccountByAccountNumber(String accountNumber) {
//        Account response = accountService.getAccountByAccountNumber(accountNumber);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//}
