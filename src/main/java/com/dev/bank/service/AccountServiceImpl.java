//package com.dev.bank.service;
//
//import com.dev.bank.exception.ResourceNotFoundException;
//import com.dev.bank.model.Account;
//import com.dev.bank.model.Card;
//import com.dev.bank.repository.AccountRepository;
//import com.dev.bank.repository.CardRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.Optional;
//
//@Service
//public class AccountServiceImpl implements AccountService{
//    private final AccountRepository accountRepository;
//
//    private final CardRepository cardRepository;
//
//    public AccountServiceImpl(AccountRepository accountRepository, CardRepository cardRepository) {
//        this.accountRepository = accountRepository;
//        this.cardRepository = cardRepository;
//    }
//
//    @Override
//    public Account getAccountByCard(String pan) {
//        Card card = cardRepository.findCardByPAN(pan)
//                .orElseThrow(() -> new ResourceNotFoundException("CARD", "PAN", pan));
//        Account account = accountRepository.findAccountByCard(card.getPAN());
//        return account;
//    }
//
//    @Override
//    public Account getAccountByAccountNumber(String accountNumber) {
//        Account account = accountRepository.findByAccountNumber(accountNumber);
//        return account;
//    }
//}
