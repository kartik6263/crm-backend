package com.resolion.crm.controller;

import com.resolion.crm.entity.AuditLog;
import com.resolion.crm.respository.AuditLogRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    @GetMapping
    public List<AuditLog> logs(@RequestParam String email, @RequestParam Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

        return auditLogRepository.findByCompanyIdOrderByIdDesc(companyId);
    }
}