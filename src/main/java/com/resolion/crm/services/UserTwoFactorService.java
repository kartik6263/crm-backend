package com.resolion.crm.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolion.crm.dpo.TotpSetupResponse;
import com.resolion.crm.entity.databaseCRM;
import com.resolion.crm.respository.crmRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTwoFactorService {

    @Autowired
    private crmRespository crmRepository;

    @Autowired
    private TotpService totpService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TotpSetupResponse beginSetup(String email) {
        databaseCRM user = crmRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret = totpService.generateSecret();
        List<String> backupCodes = totpService.generateBackupCodes();

        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(false);

        try {
            user.setBackupCodes(objectMapper.writeValueAsString(backupCodes));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to save backup codes");
        }

        crmRepository.save(user);

        String qrUrl = totpService.buildQrUrl(user.getEmail(), secret);
        return new TotpSetupResponse(secret, qrUrl, backupCodes);
    }

    public String confirmSetup(String email, String code) {
        databaseCRM user = crmRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTwoFactorSecret() == null || user.getTwoFactorSecret().isBlank()) {
            throw new RuntimeException("2FA setup not started");
        }

        boolean valid = totpService.verifyCode(user.getTwoFactorSecret(), code);
        if (!valid) {
            throw new RuntimeException("Invalid authenticator code");
        }

        user.setTwoFactorEnabled(true);
        crmRepository.save(user);

        return "Authenticator app 2FA enabled successfully";
    }

    public boolean verifyTotpOrBackupCode(String email, String code) {
        databaseCRM user = crmRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())
                && user.getTwoFactorSecret() != null
                && totpService.verifyCode(user.getTwoFactorSecret(), code)) {
            return true;
        }

        try {
            List<String> backupCodes = objectMapper.readValue(
                    user.getBackupCodes(),
                    new TypeReference<List<String>>() {}
            );

            if (backupCodes.contains(code.toUpperCase())) {
                backupCodes.remove(code.toUpperCase());
                user.setBackupCodes(objectMapper.writeValueAsString(backupCodes));
                crmRepository.save(user);
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify backup code");
        }

        return false;
    }
}