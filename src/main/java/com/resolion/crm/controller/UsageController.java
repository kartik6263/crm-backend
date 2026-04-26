package com.resolion.crm.controller;

import java.util.HashMap;
import java.util.Map;
import com.resolion.crm.entity.CompanySetting;
import com.resolion.crm.entity.CompanyUsage;
import com.resolion.crm.respository.CompanySettingRepository;
import com.resolion.crm.respository.CompanyUsageRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



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

        Map<String, Object> map = new HashMap<>();

        map.put("planName", setting.getPlanName());
        map.put("totalUsers", usage.getTotalUsers());
        map.put("maxUsers", setting.getMaxUsers());
        map.put("totalLeads", usage.getTotalLeads());
        map.put("maxLeads", setting.getMaxLeads());
        map.put("aiReportsUsed", usage.getAiReportsUsed());
        map.put("maxAiReports", setting.getMaxAiReports());
        map.put("exportsUsed", usage.getExportsUsed());
        map.put("maxExports", setting.getMaxExports());
        map.put("smsOtpUsed", usage.getSmsOtpUsed());
        map.put("maxSmsOtp", setting.getMaxSmsOtp());

        return map;

    }
}