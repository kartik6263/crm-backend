package com.resolion.crm.controller;

import com.resolion.crm.entity.CompanySetting;
import com.resolion.crm.respository.CompanySettingRepository;
import com.resolion.crm.services.AuditService;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/company-settings")
public class CompanySettingController {

    @Autowired
    private CompanySettingRepository companySettingRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public CompanySetting getSettings(@RequestParam String email, @RequestParam Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

        return companySettingRepository.findByCompanyId(companyId)
                .orElseGet(() -> {
                    CompanySetting setting = new CompanySetting();
                    setting.setCompanyId(companyId);
                    setting.setCompanyName("Resolion Company");
                    setting.setUpdatedDate(LocalDateTime.now().toString());
                    return companySettingRepository.save(setting);
                });
    }

    @PutMapping
    public CompanySetting updateSettings(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestBody CompanySetting incoming) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

        CompanySetting setting = companySettingRepository.findByCompanyId(companyId)
                .orElse(new CompanySetting());

        setting.setCompanyId(companyId);
        setting.setCompanyName(incoming.getCompanyName());
        setting.setMaxUsers(incoming.getMaxUsers());
        setting.setMaxLeads(incoming.getMaxLeads());
        setting.setAllowAiReports(incoming.getAllowAiReports());
        setting.setAllowExports(incoming.getAllowExports());
        setting.setAllowSmsOtp(incoming.getAllowSmsOtp());
        setting.setUpdatedDate(LocalDateTime.now().toString());

        CompanySetting saved = companySettingRepository.save(setting);

        auditService.log(companyId, email, "UPDATE", "COMPANY_SETTINGS",
                String.valueOf(companyId), "Company settings updated");

        return saved;
    }
}