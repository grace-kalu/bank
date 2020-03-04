package com.dev.bank.repository;

import com.dev.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository  extends JpaRepository<Customer, UUID> {

}
