package com.resolion.crm.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.accountSid:}")
    private String accountSid;

    @Value("${twilio.authToken:}")
    private String authToken;

    @Value("${twilio.phoneNumber:}")
    private String fromNumber;

        public void sendSmsOtp(String to, String messageText) {
            if (accountSid == null || accountSid.isBlank()
                    || authToken == null || authToken.isBlank()
                    || fromNumber == null || fromNumber.isBlank()) {
                System.out.println("Twilio not configured");
                    return;
                }


                try{
                    Twilio.init(accountSid, authToken);

                    Message message = Message.creator(
                            new PhoneNumber("whatsapp:+91" + to),
                            new PhoneNumber("whatsapp:" + fromNumber),
                            messageText
                    ).create();

                    System.out.println("SMS sent successfully. SID: " + message.getSid());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to send SMS OTP: " + e.getMessage());
                }
            }
        }






