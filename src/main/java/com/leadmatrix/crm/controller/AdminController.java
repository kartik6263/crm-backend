package com.leadmatrix.crm.controller;

import com.leadmatrix.crm.entity.Company;
import com.leadmatrix.crm.entity.Subscription;
import com.leadmatrix.crm.respository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        int total = subscriptionRepository.findAll()
                .stream()
                .mapToInt(sub -> "PRO".equals(sub.getPlan()) ? 500 : 0)
                .sum();

        return total;

    }

// system ststus api
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")

    public Map<String, Long> stats(){

        Map<String, Long> map = new HashMap<>();

        map.put("totalUsers", CrmRespository.count());
        map.put("totalLeads", leadmatrixRespository.count());
        map.put("totalCompanies", companyRepository.count());

        return map;

    }


}
