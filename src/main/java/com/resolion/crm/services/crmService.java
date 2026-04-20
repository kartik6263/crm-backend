package com.resolion.crm.services;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.resolion.crm.dpo.CompanyLoginResponse;
import com.resolion.crm.dpo.LoginResponse;
import com.resolion.crm.security.JwtUtility;
import com.resolion.crm.respository.crmRespository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.resolion.crm.entity.databaseCRM;
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
    CompanyAccessService companyAccessService;

    @Autowired
    private crmRespository crmRepository;

    @Autowired
    private JwtUtility jwtutil;



    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public boolean verifyPassword(String email, String rawPassword) {
        databaseCRM user = crmRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


    public CompanyLoginResponse multiCompanyLogin(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();

        databaseCRM user = crmRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtutil.generateToken(user.getEmail());
        List<Map<String, Object>> companies = companyAccessService.getUserCompanies(normalizedEmail);

        return new CompanyLoginResponse(token, user.getEmail(), companies);
    }
   /* public CompanyLoginResponse multiCompanyLoginAfter2FA(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        databaseCRM user = crmRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtutil.generateToken(user.getEmail());
        List<Map<String, Object>> companies = companyAccessService.getUserCompanies(normalizedEmail);

        return new CompanyLoginResponse(token, user.getEmail(), companies);
    }*/
    /*public CompanyLoginResponse multiCompanyLogin(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();

        databaseCRM user = crmRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtutil.generateToken(user.getEmail());
        List<Map<String, Object>> companies = companyAccessService.getUserCompanies(normalizedEmail);

        return new CompanyLoginResponse(token, user.getEmail(), companies);
    }
*/

    public String loginUser(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        Optional<databaseCRM> userOptional = crmRepository.findByEmail(normalizedEmail);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        databaseCRM user = userOptional.get();

        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtutil.generateToken(user.getEmail());
            return "Login  Successfull. Token:" + token;
        } else {
            return "Invalid Password";
        }
        }

//============================================================================================

       // @Autowired
       // private BCryptPasswordEncoder passwordEncoder;

        public String registerUser(databaseCRM user){
            String normalizedEmail = user.getEmail().trim().toLowerCase();
            user.setEmail(normalizedEmail);

            Optional<databaseCRM> existingUser = crmRepository.findByEmail(normalizedEmail);

            if (existingUser.isPresent()) {
                return "Email already registered";
            }


            // ✅ AUTO COMPANY ID GENERATE
           // long companyId = System.currentTimeMillis(); // simple unique id
           // user.setCompanyId(companyId);

            // default role
            if (user.getRole()==null || user.getRole().isBlank()) {
                user.setRole("USER");
            }

            //if (user.getCompanyId()==null) {
             //   user.setCompanyId(System.currentTimeMillis());
          //  }
            // password encode
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            crmRepository.save(user);
            return "User Registered Successfully";
        }

    public databaseCRM getUserByEmail(String email) {
        return crmRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean verifyAdminPassword(String email, String password) {
        databaseCRM user = crmRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

       // if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
          //  return false;
    //    }

        return passwordEncoder.matches(password, user.getPassword());
    }



    @Value("${google.client.id}")
    private String googleClientId;

    public LoginResponse googleLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(java.util.Collections.singletonList(googleClientId)).build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            databaseCRM user = crmRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = new databaseCRM();
                user.setName(name != null ? name : "Google User");
                user.setEmail(email);
                user.setPhone("");
                user.setPassword(passwordEncoder.encode("GOOGLE_AUTH_USER"));
                user.setRole("USER");
                crmRepository.save(user);
            }

            String token = jwtutil.generateToken(user.getEmail());
            return new LoginResponse(token, user.getRole(), user.getEmail());

        } catch (Exception e) {
            throw new RuntimeException("Google login failed: " + e.getMessage());
        }
    }
    }




