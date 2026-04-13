package com.leadmatrix.crm.controller;

import com.leadmatrix.crm.dpo.CompanyLoginResponse;
import com.leadmatrix.crm.dpo.GoogleLoginRequest;
import com.leadmatrix.crm.dpo.LoginRequest;
import com.leadmatrix.crm.dpo.LoginResponse;
import com.leadmatrix.crm.dpo.RegisterCompanyRequest;
import com.leadmatrix.crm.entity.databaseCRM;
import com.leadmatrix.crm.respository.crmRespository;
import com.leadmatrix.crm.security.JwtUtility;
import com.leadmatrix.crm.services.CompanyAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.leadmatrix.crm.services.crmService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crm")
public class CrmEntryController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private crmService crmService;



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
   /* private final crmService crmService;
    private final CompanyAccessService companyAccessService;

    public CrmEntryController(crmService crmService,
                              CompanyAccessService companyAccessService) {
        this.crmService = crmService;
        this.companyAccessService = companyAccessService;
    }*/



    private final Logger log =
            LoggerFactory.getLogger(CrmEntryController.class);

    public String loginUser(String username) {
        log.info("User login attempt");
        return "done";
    }

    /// ///////////
    /* @PostMapping("/register")
  public String registerUser(@RequestBody databaseCRM user) {
       user.setRole("USER");
     CrmService.registerUser(user);
        return CrmService.registerUser(user);
    }*/
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
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setRole("USER");

        String result = crmService.registerUser(user);

        if (!"User Registered Successfully".equalsIgnoreCase(result)) {
            return result;
        }

        databaseCRM savedUser = crmService.getUserByEmail(request.getEmail());
        companyAccessService.createCompanyWithOwner(savedUser, request.getCompanyName());

        return "Company and owner account created successfully";
    }

    @PostMapping("/login")
    public CompanyLoginResponse login(@RequestBody LoginRequest request) {

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.getPassword()
                )
        );

        return crmService.multiCompanyLogin(
                normalizedEmail,
                request.getPassword()
        );
    }
  /*  @PostMapping("/login")
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
   // public LoginResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        databaseCRM user = crmRespository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        String token = jwtUtil.generateToken(user.getEmail());

      // return new LoginResponse(token, user.getRole(), user.getEmail());


    }*/

    @PostMapping("/google-login")
    public LoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return CrmService.googleLogin(request.getIdToken());
    }

}

