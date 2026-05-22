package com.resolion.crm.controller;

import com.resolion.crm.entity.HomeDashboardStats;
import com.resolion.crm.service.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
public class HomeDashboardController {

    @Autowired
    private CompanyAccessService companyAccessService;

    @GetMapping("/stats")
    public HomeDashboardStats stats(@RequestParam String email,
                                    @RequestParam Long companyId) {

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }

        return new HomeDashboardStats(
                5,
                3,
                8,
                2,
                4,
                12
        );
    }

    @PostMapping("/reset")
    public String resetDashboard(@RequestParam String email,
                                 @RequestParam Long companyId) {

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }

        return "Dashboard reset successfully";
    }
}