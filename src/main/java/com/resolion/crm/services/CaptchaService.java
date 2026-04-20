package com.resolion.crm.services;

import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

    public boolean verifyCaptcha(String captchaToken) {
        return captchaToken != null && !captchaToken.isBlank();
    }
}
