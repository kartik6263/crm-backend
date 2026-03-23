package com.leadmatrix.crm.services;


import java.util.Optional;

import com.leadmatrix.crm.security.JwtUtility;
import com.leadmatrix.crm.respository.crmRespository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.leadmatrix.crm.entity.databaseCRM;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Service layer for writeing logic==================================================
@Service
public class crmService {

    private static final Logger log =
            LoggerFactory.getLogger(crmService.class);

    public String loginUser(String username) {
        log.info("User login attempt");
        return "done";
    }

// only admin can call this api (admin api)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create-user")

    public String createUser(@RequestBody databaseCRM user){

        crmRepository.save(user);

        return "User created";
    }




    @Autowired
    private crmRespository crmRepository;




    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String loginUser(String email, String password) {
        Optional<databaseCRM> userOptional = crmRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        databaseCRM user = userOptional.get();

        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = JwtUtility.generateToken(user.getEmail());
            return "Login  Successfull. Token:" + token;
        } else {
            return "Invalid Password";
        }
        }

//============================================================================================

       // @Autowired
       // private BCryptPasswordEncoder passwordEncoder;

        public String registerUser (databaseCRM user){
            Optional<databaseCRM> existingUser = crmRepository.findByEmail(user.getEmail());

            if (existingUser.isPresent()) {
                return "Email already registered";
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            crmRepository.save(user);
            return "User Registered Successfully";
        }
    }




