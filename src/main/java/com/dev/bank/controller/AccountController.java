
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

    @PostMapping("/transaction/credit")
   public ResponseEntity<?> makePayment(@Valid @RequestBody HashMap<String, String> data){
        String accountNumber = data.get("account-detail");
        Map<String, String> response= new HashMap<>();
    if(accountNumber.length()==10){
        Account account = accountService.findAccountByAccountNumber(accountNumber);
        response.put("name", account.getCustomer().getName());
        response.put("account-number", accountNumber);

        Double amount = Double.parseDouble((data.get("amount")));
        if(amount <= 0 ){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST).
                    body(response);
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }else{
        String cardNumber = data.get("account-detail");
        Account account = cardService.findByPAN(cardNumber).getAccount();
        response.put("name", account.getCustomer().getName());
        response.put("account-number", accountNumber);

        Double amount = Double.parseDouble((data.get("amount")));
        if(amount <= 0 ){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST).
                    body(response);
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


        /*TokenOtp tokenOtp = tokenOtpService.findByToken(Integer.parseInt(token));
        //check if token is valid
        if (tokenOtp==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        //check if token has expired
        if(!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }*/

   }


   @PostMapping("/transaction/debit")
   public ResponseEntity<?> withdraw(@Valid @RequestBody HashMap<String, String> data){
        String accountNumber = data.get("account-detail");
       Map<String, String> response= new HashMap<>();

        if (accountNumber.length()==10){

            Account account = accountService.findAccountByAccountNumber(accountNumber);
            response.put("name", account.getCustomer().getName());
            response.put("account-number", accountNumber);

            //Check if token is valid
            TokenOtp tokenOtp = tokenOtpService.findByToken(Integer.parseInt(data.get("OTP")));
            if (tokenOtp==null){
                response.put("message", "invalid token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            //check if token has expired
            if(!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
                response.put("message", "token has expired");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
            Double amount = Double.parseDouble((data.get("amount")));
       if(account.getBalance() <  amount){
           response.put("message", "insufficient funds");
           return ResponseEntity.
                   status(HttpStatus.BAD_REQUEST).
                   body(response);
       }

       account.setBalance(account.getBalance() - amount);
       accountRepository.save(account);
            response.put("message", "transaction completed");
       return ResponseEntity.status(HttpStatus.OK).body(response);
   }else{
            String cardNumber = accountNumber;
            Account account = cardService.findByPAN(cardNumber).getAccount();
            response.put("name", account.getCustomer().getName());
            response.put("account-number", account.getAccountNumber());

            TokenOtp tokenOtp = tokenOtpService.findByToken(Integer.parseInt(data.get("OTP")));
            if (tokenOtp==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            //check if token has expired
            if(!Instant.now().isBefore(tokenOtp.getTokenexpiry())){
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
            Double amount = Double.parseDouble((data.get("amount")));
            if(account.getBalance() <  amount){
                return ResponseEntity.
                        status(HttpStatus.BAD_REQUEST).
                        body(response);
            }

            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }


   @PostMapping("/transaction/verification")
   public ResponseEntity<?> verifyTransactionRequest(@RequestBody HashMap<String, String> data){

        String cardNumber = data.get("PAN");
        String  accountNumber= data.get("account-number");
       System.out.println(accountNumber);

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

            HashMap<String, String> response = new HashMap<>();

            response.put("message", "account exists, token sent to account's owner");

          return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response);
             // return new ResponseEntity<>("account exists, token sent to account's owner", HttpStatus.OK);
          }else{
              return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("account does not exist");
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


/*   public ResponseEntity<?> getAccountByCardNumber(String PAN){
        Optional<Card> card = cardRepository.findCardByPAN(PAN);
        if(!card.isPresent()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("card number doesn't exist");
        }
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(accountService.findAccountByCardPAN(PAN));
   }*/
/*
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
*/

}

