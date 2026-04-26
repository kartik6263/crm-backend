package com.resolion.crm.controller;

import com.resolion.crm.entity.CompanySetting;
import com.resolion.crm.entity.CompanyUsage;
import com.resolion.crm.respository.CompanySettingRepository;
import com.resolion.crm.respository.CompanyUsageRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    @Autowired
    private CompanyAccessService companyAccessService;

    @Autowired
    private CompanyUsageRepository companyUsageRepository;

    @Autowired
    private CompanySettingRepository companySettingRepository;

    @GetMapping
    public Map<String, Object> usage(@RequestParam String email,
                                     @RequestParam Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

        CompanyUsage usage = companyUsageRepository.findByCompanyId(companyId)
                .orElseGet(() -> {
                    CompanyUsage u = new CompanyUsage();
                    u.setCompanyId(companyId);
                    return companyUsageRepository.save(u);
                });

        CompanySetting setting = companySettingRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Company settings not found"));

        return Map.of(
                "planName", setting.getPlanName(),
                "totalUsers", usage.getTotalUsers(),
                "maxUsers", setting.getMaxUsers(),
                "totalLeads", usage.getTotalLeads(),
                "maxLeads", setting.getMaxLeads(),
                "aiReportsUsed", usage.getAiReportsUsed(),
                "maxAiReports", setting.getMaxAiReports(),
                "exportsUsed", usage.getExportsUsed(),
                "maxExports", setting.getMaxExports(),
                "smsOtpUsed", usage.getSmsOtpUsed(),
                "maxSmsOtp", setting.getMaxSmsOtp()
        );
    }
}