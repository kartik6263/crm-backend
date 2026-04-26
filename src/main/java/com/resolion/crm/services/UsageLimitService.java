package com.resolion.crm.services;

import com.resolion.crm.entity.CompanySetting;
import com.resolion.crm.entity.CompanyUsage;
import com.resolion.crm.respository.CompanySettingRepository;
import com.resolion.crm.respository.CompanyUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsageLimitService {

    @Autowired
    private CompanySettingRepository companySettingRepository;

    @Autowired
    private CompanyUsageRepository companyUsageRepository;

    private CompanyUsage usage(Long companyId) {
        return companyUsageRepository.findByCompanyId(companyId)
                .orElseGet(() -> {
                    CompanyUsage u = new CompanyUsage();
                    u.setCompanyId(companyId);
                    u.setUpdatedDate(LocalDateTime.now().toString());
                    return companyUsageRepository.save(u);
                });
    }

    private CompanySetting settings(Long companyId) {
        return companySettingRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Company settings not found"));
    }

    public void checkLeadLimit(Long companyId) {
        CompanySetting s = settings(companyId);
        CompanyUsage u = usage(companyId);

        if (u.getTotalLeads() >= s.getMaxLeads()) {
            throw new RuntimeException("Lead limit reached. Please upgrade your plan.");
        }
    }

    public void checkUserLimit(Long companyId) {
        CompanySetting s = settings(companyId);
        CompanyUsage u = usage(companyId);

        if (u.getTotalUsers() >= s.getMaxUsers()) {
            throw new RuntimeException("User limit reached. Please upgrade your plan.");
        }
    }

    public void checkAiReportLimit(Long companyId) {
        CompanySetting s = settings(companyId);
        CompanyUsage u = usage(companyId);

        if (!Boolean.TRUE.equals(s.getAllowAiReports())) {
            throw new RuntimeException("AI reports are disabled for your plan.");
        }

        if (u.getAiReportsUsed() >= s.getMaxAiReports()) {
            throw new RuntimeException("AI report limit reached. Please upgrade your plan.");
        }
    }

    public void checkExportLimit(Long companyId) {
        CompanySetting s = settings(companyId);
        CompanyUsage u = usage(companyId);

        if (!Boolean.TRUE.equals(s.getAllowExports())) {
            throw new RuntimeException("Exports are disabled for your plan.");
        }

        if (u.getExportsUsed() >= s.getMaxExports()) {
            throw new RuntimeException("Export limit reached. Please upgrade your plan.");
        }
    }

    public void checkSmsOtpLimit(Long companyId) {
        CompanySetting s = settings(companyId);
        CompanyUsage u = usage(companyId);

        if (!Boolean.TRUE.equals(s.getAllowSmsOtp())) {
            throw new RuntimeException("SMS OTP is disabled for your plan.");
        }

        if (u.getSmsOtpUsed() >= s.getMaxSmsOtp()) {
            throw new RuntimeException("SMS OTP limit reached. Please upgrade your plan.");
        }
    }

    public void incrementLeads(Long companyId) {
        CompanyUsage u = usage(companyId);
        u.setTotalLeads(u.getTotalLeads() + 1);
        u.setUpdatedDate(LocalDateTime.now().toString());
        companyUsageRepository.save(u);
    }

    public void incrementUsers(Long companyId) {
        CompanyUsage u = usage(companyId);
        u.setTotalUsers(u.getTotalUsers() + 1);
        u.setUpdatedDate(LocalDateTime.now().toString());
        companyUsageRepository.save(u);
    }

    public void incrementAiReports(Long companyId) {
        CompanyUsage u = usage(companyId);
        u.setAiReportsUsed(u.getAiReportsUsed() + 1);
        u.setUpdatedDate(LocalDateTime.now().toString());
        companyUsageRepository.save(u);
    }

    public void incrementExports(Long companyId) {
        CompanyUsage u = usage(companyId);
        u.setExportsUsed(u.getExportsUsed() + 1);
        u.setUpdatedDate(LocalDateTime.now().toString());
        companyUsageRepository.save(u);
    }

    public void incrementSmsOtp(Long companyId) {
        CompanyUsage u = usage(companyId);
        u.setSmsOtpUsed(u.getSmsOtpUsed() + 1);
        u.setUpdatedDate(LocalDateTime.now().toString());
        companyUsageRepository.save(u);
    }
}