package com.resolion.crm.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class CaptchaService {

    @Value("${turnstile.secret}")
    private String turnstileSecret;

    public boolean verifyCaptcha(String captchaToken) {
        if (captchaToken == null || captchaToken.isBlank()) {
            return false;
        }

        try {
            URL url = new URL("https://challenges.cloudflare.com/turnstile/v0/siteverify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "secret=" + turnstileSecret + "&response=" + captchaToken;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream is = conn.getInputStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(response);
                return json.optBoolean("success", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}