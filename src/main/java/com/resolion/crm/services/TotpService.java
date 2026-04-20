package com.resolion.crm.services;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TotpService {

    private final DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
    private final TimeProvider timeProvider = new SystemTimeProvider();

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public boolean verifyCode(String secret, String code) {
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secret, code);
    }

    public String buildQrUrl(String email, String secret) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer("Resolion CRM")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        return Utils.getDataUriForImage(
                new QrDataFactory().newBuilder(data).build(200)
        );
    }

    public List<String> generateBackupCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            codes.add(UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        }
        return codes;
    }
}