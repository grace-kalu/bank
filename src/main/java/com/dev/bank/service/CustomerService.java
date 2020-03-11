package com.dev.bank.service;

import com.dev.bank.model.Customer;
import com.dev.bank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    public Customer createCustomer(HashMap<String, String> request){
        Customer customer = new Customer();
        customer.setName(request.get("name"));
        customer.setEmail(request.get("email"));
        customer.setPhoneNumber(request.get("phoneNumber"));
        customer.setAddress(request.get("address"));
        customer.setDateOfBirth(request.get("dateOfBirth"));
        return customerRepository.save(customer);
    }

}
