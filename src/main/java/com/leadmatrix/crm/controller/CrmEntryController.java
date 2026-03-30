package com.leadmatrix.crm.controller;

import com.leadmatrix.crm.dpo.LoginRequest;
import com.leadmatrix.crm.dpo.LoginResponse;
import com.leadmatrix.crm.entity.databaseCRM;
import com.leadmatrix.crm.respository.crmRespository;
import com.leadmatrix.crm.security.JwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.leadmatrix.crm.services.crmService;

@RestController
@RequestMapping("/api/crm")
public class CrmEntryController {

    private final Logger log =
            LoggerFactory.getLogger(CrmEntryController.class);

    public String loginUser(String username) {
        log.info("User login attempt");
        return "done";
    }

    @Autowired
    private crmRespository crmRespository;

    @Autowired
    private crmService CrmService;

    /// ///////////
    @PostMapping("/register")
    public String registerUser(@RequestBody databaseCRM user) {
        CrmService.registerUser(user);
        return "User Registered Successfully";
    }


    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtil;
    // private final crmRespository crmRespository;

    public CrmEntryController(AuthenticationManager authenticationManager,
                              JwtUtility jwtUtil,
                              crmRespository crmRespository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.crmRespository = crmRespository;
    }


    /*@PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        System.out.println("LOGIN START: " + request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        System.out.println("AUTH SUCCESS");

        databaseCRM user = crmRespository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        System.out.println("USER FETCHED: " + user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());

        System.out.println("TOKEN GENERATED");

        return new LoginResponse(token, user.getRole(), user.getEmail());
    }*/

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("LOGIN START: " + request.getEmail());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            System.out.println("AUTH SUCCESS");

            databaseCRM user = crmRespository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found in DB"));
            System.out.println("USER FETCHED: " + user.getEmail());

            // TEMP TEST: token generation skip
            return ResponseEntity.ok("LOGIN SUCCESS WITHOUT TOKEN");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("LOGIN FAILED: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }
}

