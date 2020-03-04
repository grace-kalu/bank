package com.dev.bank.repository;

import com.dev.bank.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Card findCardByPAN(String PAN);
}
