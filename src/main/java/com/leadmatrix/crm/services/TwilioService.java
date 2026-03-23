package com.leadmatrix.crm.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.sid:}")
    private String accountSid;

    @Value("${twilio.token:}")
    private String authToken;

    @Value("${twilio.number:}")
    private String fromNumber;

        public void sendWhatsAppMessage(String to, String messageText) {


            if(accountSid == null || accountSid.isEmpty()) {
                System.out.println("Twilio not configured");
                return;
            }


            Twilio.init(accountSid, authToken);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:+91" + to),
                    new PhoneNumber("whatsapp:" + fromNumber),
                    messageText
            ).create();

            System.out.println("Message SID: " + message.getSid());
        }
    }

