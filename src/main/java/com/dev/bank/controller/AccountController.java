
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
package com.dev.bank.controller;

import com.dev.bank.model.Account;
import com.dev.bank.model.Card;
import com.dev.bank.model.Customer;
import com.dev.bank.model.TokenOtp;
import com.dev.bank.repository.AccountRepository;
import com.dev.bank.repository.CardRepository;
import com.dev.bank.service.AccountService;
import com.dev.bank.service.CardService;
import com.dev.bank.service.CustomerService;
import com.dev.bank.service.TokenOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenOtpService tokenOtpService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CardService cardService;

    /*Endpoint for getting all accounts. should be protected*/
    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts(
            @RequestParam(value = "page", defaultValue ="0") int page,
            @RequestParam(value = "size", defaultValue ="3")  int size) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        accountService.getAccountsList(page, size)
                        .getContent()
                    );

    }

/* EndPoint for creating a new account*/
    @PostMapping("/accounts")
    public ResponseEntity<String> createAccount(@Valid @RequestBody HashMap<String, String> request) {
        Customer customer = customerService.createCustomer(request);
        /*Account created and token sent to the customer/user*/
       Account newAccount = accountService.createAccount(request, customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newAccount.getAccountNumber());
    }

    /* Route for confirming a newly created account after token was sent and returned
    @PostMapping("/account/confirmation")
    public ResponseEntity<?> confirmAccount(
            @RequestBody int token,
            @RequestBody String accountNumber
    ){
        TokenOtp tokenOtp = tokenOtpService.findByToken(token);
        if (!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
            accountRepository.deleteByAccountNumber(
                  //  tokenOtp.getAccountNumber()
                    accountNumber
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token has expired");
            //if(Instant.now() )
        }
        return ResponseEntity.status(HttpStatus.OK).body("User account is authenticated and verified");
    }
*/
    /* Route for payment and crediting accounts*/
    @PostMapping("/transaction/credit")
   public ResponseEntity<?> makePayment(@Valid @RequestBody HashMap<String, String> request){
        String accountNumber = request.get("account-detail");
        Map<String, String> response= new HashMap<>();
        Account account;
        if(accountNumber.length()==10){
            System.out.println("a");
            account = accountService.findAccountByAccountNumber(accountNumber);
            System.out.println("b");
        } else {
            System.out.println("c");
            String cardNumber = request.get("account-detail");
            account = cardService.findByPAN(cardNumber).getAccount();
            System.out.println("d");
        }
        response.put("name", account.getCustomer().getName());
        response.put("account-number", accountNumber);
        Double amount = Double.parseDouble((request.get("amount")));
        if(amount <= 0 ){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST).
                    body(response);
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(response);

   }


   @PostMapping("/transaction/debit")
   public ResponseEntity<?> withdraw(@Valid @RequestBody HashMap<String, String> request){
       String accountNumber = request.get("account-detail");
       Map<String, String> response= new HashMap<>();

       String token = request.get("OTP");
       System.out.println(token);
       Optional<TokenOtp> tokenOtp = tokenOtpService.findByToken(token);
//       System.out.println(tokenOtp.getToken());
       if (tokenOtp.isEmpty()){
           response.put("message", "token expired or incorrect");
           System.out.println("token expired");
           return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(response);
       }
       Account account;
       if(accountNumber.length()==10){
           account = accountService.findAccountByAccountNumber(accountNumber);
       } else {
           String cardNumber = request.get("account-detail");
           account = cardService.findByPAN(cardNumber).getAccount();
       }

       response.put("name", account.getCustomer().getName());
       response.put("account-number", accountNumber);
            Double amount = Double.parseDouble((request.get("amount")));
       if(account.getBalance() <  amount){
           response.put("message", "insufficient funds");
           System.out.println("insufficient funds");
           return ResponseEntity.
                   status(HttpStatus.BAD_REQUEST).
                   body(response);
       }

       account.setBalance(account.getBalance() - amount);
       accountRepository.save(account);
       response.put("message", "transaction completed");
       System.out.println("oshey owo wole");
       return ResponseEntity.status(HttpStatus.OK).body(response);

    }


   @PostMapping("/transaction/verification")
   public ResponseEntity<?> verifyTransactionRequest(@RequestBody HashMap<String, String> data){

        String cardNumber = data.get("PAN");
        String  accountNumber= data.get("account-number");
        System.out.println(accountNumber);
        HashMap<String, String> response = new HashMap<>();
       //validate account number field since we are checking either account number or card details
        if(accountNumber != null){
            //get the account
          Account account=  accountService.findAccountByAccountNumber(accountNumber);
          if (account != null) {
              //send a token to the user/customer
                tokenOtpService.createTokenOrOtp(account);
            response.put("message", "account exists, token sent to account's owner");
            return ResponseEntity
                  .status(HttpStatus.OK)
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(response);
             // return new ResponseEntity<>("account exists, token sent to account's owner", HttpStatus.OK);
          } else {
              response.put("message", "account does not exist");
              return ResponseEntity
                      .status(HttpStatus.UNPROCESSABLE_ENTITY)
                      .body(response);
          }
        }

       //validate card number field since we are checking either account number or card details
       if(cardNumber != null){

           Card card = cardService.findByPAN(cardNumber);
           Account account=  accountService
                   .findAccountByAccountNumber(card.getAccount().getAccountNumber());
           if(account != null){
               System.out.println(account.getAccountNumber());
               tokenOtpService.createTokenOrOtp(account);
               response.put("message", "account exists, token sent to account's owner");               return ResponseEntity
                       .status(HttpStatus.OK)
                       .contentType(MediaType.APPLICATION_JSON)
                       .body(response);
           } else {
               response.put("message", "invalid accountnumber");
               return ResponseEntity
                       .status(HttpStatus.BAD_REQUEST)
                       .body(response);
           }
       }
       response.put("message", "invalid account number or card");
   return ResponseEntity
           .status(HttpStatus.BAD_REQUEST)
           .body(response);
    }



}

