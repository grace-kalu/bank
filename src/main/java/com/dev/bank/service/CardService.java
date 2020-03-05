package com.dev.bank.service;

import com.dev.bank.model.Card;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface CardService {
    Card findByPAN(String PAN);
}
