package com.dev.bank.config;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import java.util.Random;

public class TwillioSms {

    // Find your Account Sid and Token at twilio.com/console

    public static final String ACCOUNT_SID="AC8bd1ffe1c29caf2fdc8930e81e0a0a46";

    public static final String AUTH_TOKEN= "8bc96dbc415f2be4692786fae349a84e";

    // Create a phone number in the Twilio console
    public static final String TWILIO_NUMBER = "+18304444321";

    public static String sendToken(String phoneNumber){
        String token = generateToken();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(TWILIO_NUMBER),
                "please confirm your token" + token)
                .create();
        return token;
    }
    public static String generateToken(){
        int token = new Random().nextInt(999999);
        return String.valueOf(token);
    }

}
