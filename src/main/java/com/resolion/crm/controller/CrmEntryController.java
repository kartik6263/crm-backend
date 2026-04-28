package com.resolion.crm.controller;

import com.resolion.crm.dpo.*;
import com.resolion.crm.entity.databaseCRM;
import com.resolion.crm.respository.crmRespository;
import com.resolion.crm.security.JwtUtility;
import com.resolion.crm.services.CompanyAccessService;
//import com.resolion.crm.services.SignupFlowService;
//import com.resolion.crm.services.UserTwoFactorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.resolion.crm.services.crmService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crm")
public class CrmEntryController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private crmService crmService;

   // @Autowired
    //SignupFlowService signupFlowService;

//    @Autowired
//    private UserTwoFactorService userTwoFactorService;



    private final crmRespository crmRespository;
    private final CompanyAccessService companyAccessService;
    private final crmService CrmService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtil;


    public CrmEntryController(AuthenticationManager authenticationManager,
                              JwtUtility jwtUtil,
                              crmRespository crmRespository,
                              CompanyAccessService companyAccessService,
                              crmService crmService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.crmRespository = crmRespository;
        this.CrmService = crmService;
        this.companyAccessService = companyAccessService;
    }


    private final Logger log =
            LoggerFactory.getLogger(CrmEntryController.class);

    public String loginUser(String username) {
        log.info("User login attempt");
        return "done";
    }

    /// ///////////

    @PostMapping("/register")
    public String registerUser(@RequestBody databaseCRM user) {
        user.setRole("USER");
        user.setEmail(user.getEmail().trim().toLowerCase());
        return CrmService.registerUser(user);
    }

    @PostMapping("/register-company")
    public String registerCompanyUser(@RequestBody RegisterCompanyRequest request) {
        databaseCRM user = new databaseCRM();
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setRole("USER");

        String result = crmService.registerUser(user);

        if (!"User Registered Successfully".equalsIgnoreCase(result)) {
            return result;
        }

        databaseCRM savedUser = crmService.getUserByEmail(request.getEmail().trim().toLowerCase());
        companyAccessService.createCompanyWithOwner(savedUser, request.getCompanyName());

        return "Company and owner account created successfully";
    }


/*  two factor and otp based
    @PostMapping("/start-signup")
    public String startSignup(@RequestBody StartSignupRequest request) {
        return signupFlowService.startSignup(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getCompanyName(),
                request.getCaptchaToken()
        );
    }



    @PostMapping("/verify-email-otp")
    public String verifyEmailOtp(@RequestBody VerifyOtpRequest request) {
        return signupFlowService.verifyEmailOtp(request.getEmail(), request.getOtp());
    }

    @PostMapping("/verify-phone-otp")
    public String verifyPhoneOtp(@RequestBody PhoneOtpRequest request) {
        return signupFlowService.verifyPhoneOtp(request.getPhone(), request.getOtp());
    }

    @PostMapping("/complete-signup")
    public String completeSignup(@RequestBody VerifyOtpRequest request) {
        return signupFlowService.completeSignup(request.getEmail());
    }

    @PostMapping("/login-step1")
    public String loginStep1(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        signupFlowService.sendTwoFactorOtp(request.getEmail().trim().toLowerCase());
        return "2FA OTP sent";
    }
    @PostMapping("/login-step2")
    public CompanyLoginResponse loginStep2(@RequestBody TwoFactorVerifyRequest request) {
        signupFlowService.verifyTwoFactorOtp(request.getEmail(), request.getOtp());
        return crmService.multiCompanyLoginAfter2FA(request.getEmail());
    }

    @GetMapping("/2fa/setup")
    public TotpSetupResponse setupAuthenticator(@RequestParam String email) {
        return userTwoFactorService.beginSetup(email);
    }

    @PostMapping("/2fa/confirm")
    public String confirmAuthenticator(@RequestBody EnableTotpRequest request) {
        return userTwoFactorService.confirmSetup(request.getEmail(), request.getCode());
    }

    @PostMapping("/login-totp")
    public CompanyLoginResponse loginWithTotp(@RequestBody TotpLoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (!crmService.verifyPassword(email, request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        boolean valid = userTwoFactorService.verifyTotpOrBackupCode(email, request.getCode());
        if (!valid) {
            throw new RuntimeException("Invalid authenticator or backup code");
        }

        return crmService.multiCompanyLoginAfter2FA(email);
    }

*/

    @PostMapping("/login")
    public CompanyLoginResponse multiCompanyLogin(String email, String password) {
        databaseCRM user = crmRespository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        List<Map<String, Object>> companies = companyAccessService.getUserCompanies(email);

        return new CompanyLoginResponse(token, user.getEmail(), companies);
    }
    public LoginResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        databaseCRM user = crmRespository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        String token = jwtUtil.generateToken(user.getEmail());

       return new LoginResponse(token, user.getRole(), user.getEmail());


    }

    @PostMapping("/google-login")
    public LoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return CrmService.googleLogin(request.getIdToken());
    }

}

