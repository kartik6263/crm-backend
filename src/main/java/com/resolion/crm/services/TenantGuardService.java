package com.resolion.crm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantGuardService {

    @Autowired
    private CompanyAccessService companyAccessService;

    public void check(String email, Long companyId) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (companyId == null) {
            throw new RuntimeException("Company ID is required");
        }

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

    }
}