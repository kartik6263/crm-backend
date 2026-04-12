package com.leadmatrix.crm.controller;

import com.leadmatrix.crm.dpo.AdminVerifyRequest;
import com.leadmatrix.crm.entity.Company;
import com.leadmatrix.crm.entity.LeadmatrixEntity;
import com.leadmatrix.crm.entity.Subscription;
import com.leadmatrix.crm.entity.databaseCRM;
import com.leadmatrix.crm.respository.*;
import com.leadmatrix.crm.services.crmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private crmRespository CrmRespository;

    @Autowired
    private LeadmatrixRespository leadmatrixRespository;

    @Autowired
    private crmService crmService;


    // get all companies
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/companies")
    public List<Company> getAllCompanies(){
        return companyRepository.findAll();
    }

    // get all subscription
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/subscriptions")
    public List<Subscription> getSubscriptions(){
        return subscriptionRepository.findAll();
    }

    // total revenue api
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue")
    public int totalRevenue(){
        return subscriptionRepository.findAll()
                .stream()
                .mapToInt(sub -> "PRO".equals(sub.getPlan()) ? 500 : 0)
                .sum();
    }

// system ststus api
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")

    public Map<String, Long> stats(@RequestParam String email) {
        databaseCRM admin = crmService.getUserByEmail(email);

        Map<String, Long> map = new HashMap<>();
        map.put("totalUsers", CrmRespository.findAll().stream()
                .filter(u -> admin.getCompanyId().equals(u.getCompanyId()))
                .count());
        map.put("totalLeads", leadmatrixRespository.countByCompanyId(admin.getCompanyId()));
        map.put("totalCompanies", companyRepository.count());
        return map;
    }

   /* public Map<String, Long> stats(){
        Map<String, Long> map = new HashMap<>();
        map.put("totalUsers", CrmRespository.count());
        map.put("totalLeads", leadmatrixRespository.count());
        map.put("totalCompanies", companyRepository.count());
        return map;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody databaseCRM user) {
        return ResponseEntity.ok(crmService.registerUser(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<databaseCRM> getAllUsers() {
        return CrmRespository.findAll();
    }*/

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestParam String adminEmail, @RequestBody databaseCRM user) {
        databaseCRM admin = crmService.getUserByEmail(adminEmail);

        // new user gets same companyid
        user.setCompanyId(admin.getCompanyId());
        return ResponseEntity.ok(crmService.registerUser(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<databaseCRM> getAllUsers(@RequestParam String email) {
        databaseCRM admin = crmService.getUserByEmail(email);

        return CrmRespository.findAll().stream()
                .filter(u -> admin.getCompanyId().equals(u.getCompanyId()))
                .toList();
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyAdminPassword(@RequestBody AdminVerifyRequest request) {
        boolean valid = crmService.verifyAdminPassword(request.getEmail(), request.getPassword());

        if (!valid) {
            return ResponseEntity.status(401).body("Invalid admin password");
        }
        return ResponseEntity.ok("Admin verified");
    }


}
