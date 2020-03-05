package com.dev.bank.controller;

import com.dev.bank.service.CustomerEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bankA")
public class RetrieveCustomerEmailController {

    private CustomerEmailService customerEmailService;

    @Autowired
    public RetrieveCustomerEmailController(CustomerEmailService customerEmailService) {
        this.customerEmailService = customerEmailService;
    }

    @GetMapping("/{accountNumber}/email")
    public ResponseEntity<String> getCustomerEmailByAccountNumber(@PathVariable("accountNumber") String accountNumber){
        String email = customerEmailService.findCustomerEmail(accountNumber);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}/phone")
    public ResponseEntity<String> getCustomerPhoneByAccountNumber(@PathVariable("accountNumber") String accountNumber){
        String email = customerEmailService.findCustomerEmail(accountNumber);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}/otp")
    public ResponseEntity<String> createOtp(@PathVariable("accountNumber") String accountNumber){
        String otp = customerEmailService.GenerateCustomerToken(accountNumber);
        return new ResponseEntity<String>(otp, HttpStatus.OK);
    }
}
