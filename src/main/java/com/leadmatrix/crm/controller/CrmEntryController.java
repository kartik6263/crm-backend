package com.leadmatrix.crm.controller;

import com.leadmatrix.crm.dpo.LoginRequest;
import com.leadmatrix.crm.entity.databaseCRM;
import com.leadmatrix.crm.respository.crmRespository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.leadmatrix.crm.services.crmService;

@RestController
@RequestMapping("/api/crm")
@CrossOrigin
public class CrmEntryController {

    private static final Logger log =
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

     //Login API
     @PostMapping("/login")
     public String loginUser(@RequestBody LoginRequest request){
         return CrmService.loginUser(request.getEmail(), request.getPassword());
     }



    }

