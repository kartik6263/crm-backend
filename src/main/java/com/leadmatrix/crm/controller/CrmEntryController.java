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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.leadmatrix.crm.services.crmService;

@RestController
@RequestMapping("/api/crm")
@CrossOrigin(origins = "https://spiffy-boba-421b79.netlify.app")
public class CrmEntryController {

    private final Logger log =
            LoggerFactory.getLogger(CrmEntryController.class);

    public String loginUser(String username) {
        log.info("User login attempt");
        return "done";
    }

      @Autowired
       private  crmRespository crmRespository;

       @Autowired
       private crmService CrmService;
/// ///////////
      @PostMapping("/register")
      public String registerUser(@RequestBody databaseCRM  user) {
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
    /*public CrmEntryController(AuthenticationManager authenticationManager, JwtUtility jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
     public String login(@RequestBody LoginRequest user) {
         authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
         );

        String token = jwtUtil.generateToken(user.getEmail());
        return "Login Successful. Token: " + token;
     }*/


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        databaseCRM user = crmRespository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(token, user.getRole(), user.getEmail());
    }

}

