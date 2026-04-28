/*package com.resolion.crm.services;

import com.resolion.crm.entity.SignupVerification;
import com.resolion.crm.entity.TwoFactorCode;
import com.resolion.crm.entity.databaseCRM;
import com.resolion.crm.respository.SignupVerificationRepository;
import com.resolion.crm.respository.TwoFactorCodeRepository;
import com.resolion.crm.respository.crmRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SignupFlowService {

    @Autowired
    private SignupVerificationRepository signupVerificationRepository;

    @Autowired
    private TwoFactorCodeRepository twoFactorCodeRepository;

    @Autowired
    private crmRespository crmRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private CompanyAccessService companyAccessService;

    public String startSignup(String name, String email, String phone, String password, String companyName, String captchaToken) {
        String normalizedEmail = email.trim().toLowerCase();

      //  email = email.trim().toLowerCase();

        if (crmRepository.findByEmail(normalizedEmail).isPresent()) {
            return "Email already registered";
        }

        if (!captchaService.verifyCaptcha(captchaToken)) {
            return "Captcha verification failed";
        }

        String emailOtp = otpService.generateOtp();
        String phoneOtp = otpService.generateOtp();

        System.out.println("Generated Email OTP = " + emailOtp);
        System.out.println("Generated Phone OTP = " + phoneOtp);


        SignupVerification signup = signupVerificationRepository.findByEmail(normalizedEmail)
                .orElse(new SignupVerification());

        signup.setName(name);
        signup.setEmail(normalizedEmail);
        signup.setPhone(phone);
        signup.setPasswordHash(passwordEncoder.encode(password));
        signup.setCompanyName(companyName);
        signup.setEmailOtp(emailOtp);
        signup.setPhoneOtp(phoneOtp);
        signup.setCaptchaVerified(true);
        signup.setEmailVerified(false);
        signup.setPhoneVerified(false);
        signup.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        signupVerificationRepository.save(signup);

        System.out.println("Saved signup verification");

        System.out.println("CALLING PHONE OTP SEND");
        twilioService.sendSmsOtp(phone, "Your CRM Phone OTP is: " + phoneOtp);

        System.out.println("CALLING EMAIL OTP SEND");
        emailService.sendEmail(normalizedEmail, "Your CRM Email OTP", "Your OTP is: " + emailOtp);

       // emailService.sendEmail(normalizedEmail, "Your CRM Email OTP", "Your OTP is: " + emailOtp);
      //  twilioService.sendSmsOtp(phone, "Your CRM Phone OTP is: " + phoneOtp);

        return "OTP sent to email and phone";
    }

    public String verifyEmailOtp(String email, String otp) {
        SignupVerification signup = signupVerificationRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Signup request not found"));

        if (signup.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!signup.getEmailOtp().equals(otp)) {
            throw new RuntimeException("Invalid email OTP");
        }

        signup.setEmailVerified(true);
        signupVerificationRepository.save(signup);

        return "Email verified";
    }

    public String verifyPhoneOtp(String phone, String otp) {
        SignupVerification signup = signupVerificationRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Signup request not found"));

        if (signup.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!signup.getPhoneOtp().equals(otp)) {
            throw new RuntimeException("Invalid phone OTP");
        }

        signup.setPhoneVerified(true);
        signupVerificationRepository.save(signup);

        return "Phone verified";
    }

    public String completeSignup(String email) {
        SignupVerification signup = signupVerificationRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Signup request not found"));

        if (!Boolean.TRUE.equals(signup.getCaptchaVerified()) ||
                !Boolean.TRUE.equals(signup.getEmailVerified()) ||
                !Boolean.TRUE.equals(signup.getPhoneVerified())) {
            throw new RuntimeException("Complete all verifications first");
        }

        databaseCRM user = new databaseCRM();
        user.setName(signup.getName());
        user.setEmail(signup.getEmail());
        user.setPhone(signup.getPhone());
        user.setPassword(signup.getPasswordHash());
        user.setRole("USER");

        crmRepository.save(user);

        databaseCRM savedUser = crmRepository.findByEmail(signup.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after save"));

        companyAccessService.createCompanyWithOwner(savedUser, signup.getCompanyName());

        signupVerificationRepository.delete(signup);

        return "Account created successfully";
    }


    public String sendLoginOtp(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        databaseCRM user = crmRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = otpService.generateOtp();

        TwoFactorCode code = new TwoFactorCode();
        code.setEmail(user.getEmail());
        code.setOtp(otp);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        code.setUsed(false);

        twoFactorCodeRepository.save(code);
        emailService.sendEmail(user.getEmail(), "Your CRM Login OTP", "Your 2FA OTP is: " + otp);

        return "2FA OTP sent";
    }

    public boolean verifyLoginOtp(String email, String otp) {
        TwoFactorCode code = twoFactorCodeRepository.findTopByEmailAndUsedFalseOrderByIdDesc(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("2FA code not found"));

        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!code.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        code.setUsed(true);
        twoFactorCodeRepository.save(code);

        return true;
    }



    public String sendTwoFactorOtp(String email) {
        email = email.trim().toLowerCase();

        String otp = otpService.generateOtp();

        TwoFactorCode code = new TwoFactorCode();
        code.setEmail(email);
        code.setOtp(otp);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        code.setUsed(false);

        twoFactorCodeRepository.save(code);
        emailService.sendEmail(email, "Your CRM 2FA Code", "Your login OTP is: " + otp);

        return "2FA OTP sent";
    }

    public boolean verifyTwoFactorOtp(String email, String otp) {
        TwoFactorCode code = twoFactorCodeRepository
                .findTopByEmailAndUsedFalseOrderByIdDesc(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("2FA code not found"));

        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("2FA OTP expired");
        }

        if (!code.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid 2FA OTP");
        }

        code.setUsed(true);
        twoFactorCodeRepository.save(code);

        return true;
    }
} */