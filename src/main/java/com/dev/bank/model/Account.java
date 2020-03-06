package com.dev.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
//import java.util.Long;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "accountNumber")
})

public class Account extends DateAudit {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotNull
    @NotBlank
    @Size(min= 10, max= 10)
    private String accountNumber;


    @NotNull
    private Double balance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /*@JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Card card;*/

    @Column(nullable = true)
    private int token;

    public Account() {
    }
    public Account(String accountNumber, Customer customer){
        this();
        this.accountNumber = accountNumber;
        this.customer = customer;
        //this.card=null;
    }

    public Long getId() {
        return id;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

   /* public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }*/
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", customer=" + customer +
                '}';
    }
}
