package com.dev.bank.service;

import com.dev.bank.exception.ResourceNotFoundException;
import com.dev.bank.model.Card;
import com.dev.bank.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class CardServiceImpl implements CardService{
    private CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository){
        this.cardRepository = cardRepository;
    }


    @Override
    public Card findByPAN(String PAN) {
        Card card = cardRepository.findCardByPAN(PAN);
        if(card != null){
            return card;
        }
        else throw new ResourceNotFoundException("Card", "Pan", PAN);
    }
}
