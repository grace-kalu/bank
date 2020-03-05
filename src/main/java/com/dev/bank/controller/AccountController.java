
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

import com.dev.bank.config.TwillioSms;
import com.dev.bank.model.Account;
import com.dev.bank.model.Card;
import com.dev.bank.model.Customer;
import com.dev.bank.model.TokenOtp;
import com.dev.bank.repository.AccountRepository;
import com.dev.bank.repository.CardRepository;
import com.dev.bank.repository.TokenOtpRepository;
import com.dev.bank.service.AccountService;
import com.dev.bank.service.CardService;
import com.dev.bank.service.TokenOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
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
    private TokenOtpRepository tokenOtpRepository;
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

/* Route for creating a new account*/
    @PostMapping("/accounts")
    public ResponseEntity<String> createAccount(@Valid @RequestBody String accountNumber,
                                 @Valid @RequestBody Customer customer) {

        /*Account created and token sent to the customer/user*/
       Account newAccount = accountService.createAccount(accountNumber, customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newAccount.getAccountNumber());
    }

    /* Route for confirming a newly created account after token was sent and returned*/
    @PostMapping("/account/confirmation")
    public ResponseEntity<?> confirmAccount(
            @RequestBody int token,
            @RequestBody String accountNumber
    ){
        TokenOtp tokenOtp = tokenOtpService.findByToken(token);
        if (tokenOtp==null){
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

    @PutMapping("/payments/bank")
   public ResponseEntity<?> makePayment(@Valid @RequestBody String accountNumber,
                                        @Valid @RequestBody Double amount,
                                        @Valid @RequestBody String token){

        Map<String, String> response= new HashMap<>();

        Account account = accountService.findAccountByAccountNumber(accountNumber);
        response.put("name", account.getCustomer().getName());
        response.put("account-number", accountNumber);

        TokenOtp tokenOtp = tokenOtpService.findByToken(Integer.parseInt(token));
        //check if token is valid
        if (tokenOtp==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        //check if token has expired
        if(!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }

        if(amount <= 0 ){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST).
                    body(response);
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(response);
   }

    @PutMapping("/payments/card")
    public ResponseEntity<?> makePaymentByCard(@Valid @RequestBody String cardNumber,
                                         @Valid @RequestBody Double amount,
                                         @Valid @RequestBody String token){
        //check if token is valid
        Map<String, String> response= new HashMap<>();

        Account account = cardService.findByPAN(cardNumber).getAccount();
        response.put("name", account.getCustomer().getName());
        response.put("account-number", account.getAccountNumber());

        //Check if token is valid
        TokenOtp tokenOtp = tokenOtpService.findByToken(Integer.parseInt(token));
        if (tokenOtp==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        //check if token has expired
        if(!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }

        if(amount <= 0 ){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST).
                    body(response);
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

   @PutMapping("/withdrawals/bank")
   public ResponseEntity<?> withdraw(@Valid @RequestBody String accountNumber,
                                     @Valid @RequestBody double amount,
                                     @Valid @RequestBody String token){

       Map<String, String> response= new HashMap<>();

       Account account = accountService.findAccountByAccountNumber(accountNumber);
       response.put("name", account.getCustomer().getName());
       response.put("account-number", accountNumber);

       //Check if token is valid
       TokenOtp tokenOtp = tokenOtpService.findByToken(Integer.parseInt(token));
       if (tokenOtp==null){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
       }
       //check if token has expired
       if(!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
           return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
       }


       if(account.getBalance() < amount ){
           return ResponseEntity.
                   status(HttpStatus.BAD_REQUEST).
                   body(response);
       }

       account.setBalance(account.getBalance() - amount);
       accountRepository.save(account);

       return ResponseEntity.status(HttpStatus.OK).body(response);
   }

    @PutMapping("/withdrawals/card")
    public ResponseEntity<?> withdrawByCard(@Valid @RequestBody String cardNumber,
                                      @Valid @RequestBody double amount,
                                      @Valid @RequestBody String token){

        Map<String, String> response= new HashMap<>();

        Account account = cardService.findByPAN(cardNumber).getAccount();
        response.put("name", account.getCustomer().getName());
        response.put("account-number", account.getAccountNumber());

        //Check if token is valid
        TokenOtp tokenOtp = tokenOtpService.findByToken(Integer.parseInt(token));
        if (tokenOtp==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        //check if token has expired
        if(!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }

        // business logic
        if(account.getBalance() < amount ){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST).
                    body(response);
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

   @PostMapping("/transaction/verification")
   public ResponseEntity<?> verifyTransactionRequest(@RequestBody String accountNumber,
                                                     @RequestBody String cardNumber){
        //validate accountnumber field since we are checking either account number or card details
        if(accountNumber != null){
            //get the account
          Account account=  accountService.findAccountByAccountNumber(accountNumber);
          if(account != null){
              //send a token to the user/customer
            int token =  TwillioSms.sendToken(account.getCustomer().getPhoneNumber());
            // create a new token entity and persist it to database
            TokenOtp tokenOtp = new TokenOtp(accountNumber);
            tokenOtp.setToken(token);
            tokenOtpRepository.save(tokenOtp);

            return ResponseEntity.status(HttpStatus.OK).body("account exists, token sent to account's owner");
          }else{
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid accountnumber");
          }
        }

       //validate cardnumber field since we are checking either account number or card details
       if(cardNumber != null){
           Card card = cardService.findByPAN(cardNumber);
           Account account=  accountService.findAccountByAccountNumber(card.getAccount().getAccountNumber());
           if(account != null){
               int token =  TwillioSms.sendToken(account.getCustomer().getPhoneNumber());
               TokenOtp tokenOtp = new TokenOtp(accountNumber);
               tokenOtp.setToken(token);
               tokenOtpRepository.save(tokenOtp);
               return ResponseEntity.status(HttpStatus.OK).body("account exists, token sent to account's owner");
           }else{
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid accountnumber");
           }
       }
   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid accountnumber or cardnumber");
    }


   public ResponseEntity<?> getAccountByCardNumber(String PAN){
        Optional<Card> card = cardRepository.findCardByPAN(PAN);
        if(!card.isPresent()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("card number doesn't exist");
        }
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(accountService.findAccountByCardPAN(PAN));
   }
}

