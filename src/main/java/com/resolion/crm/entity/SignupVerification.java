/*package com.resolion.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "signup_verifications")
public class SignupVerification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String companyName;

    private String emailOtp;
    private String phoneOtp;

    private Boolean emailVerified = false;
    private Boolean phoneVerified = false;
    private Boolean captchaVerified = false;

    private LocalDateTime expiresAt;

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmailOtp() { return emailOtp; }
    public void setEmailOtp(String emailOtp) { this.emailOtp = emailOtp; }

    public String getPhoneOtp() { return phoneOtp; }
    public void setPhoneOtp(String phoneOtp) { this.phoneOtp = phoneOtp; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public Boolean getPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(Boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public Boolean getCaptchaVerified() { return captchaVerified; }
    public void setCaptchaVerified(Boolean captchaVerified) { this.captchaVerified = captchaVerified; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}*/