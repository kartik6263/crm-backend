package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company_settings")
public class CompanySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String companyName;

    private Integer maxUsers = 5;
    private Integer maxLeads = 500;

    private Boolean allowAiReports = true;
    private Boolean allowExports = true;
    private Boolean allowSmsOtp = true;

    private String updatedDate;


    private String planName = "FREE";
    private Integer maxAiReports = 5;
    private Integer maxExports = 10;
    private Integer maxSmsOtp = 20;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }

    public Integer getMaxLeads() { return maxLeads; }
    public void setMaxLeads(Integer maxLeads) { this.maxLeads = maxLeads; }

    public Boolean getAllowAiReports() { return allowAiReports; }
    public void setAllowAiReports(Boolean allowAiReports) { this.allowAiReports = allowAiReports; }

    public Boolean getAllowExports() { return allowExports; }
    public void setAllowExports(Boolean allowExports) { this.allowExports = allowExports; }

    public Boolean getAllowSmsOtp() { return allowSmsOtp; }
    public void setAllowSmsOtp(Boolean allowSmsOtp) { this.allowSmsOtp = allowSmsOtp; }

    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Integer getMaxAiReports() { return maxAiReports; }
    public void setMaxAiReports(Integer maxAiReports) { this.maxAiReports = maxAiReports; }

    public Integer getMaxExports() { return maxExports; }
    public void setMaxExports(Integer maxExports) { this.maxExports = maxExports; }

    public Integer getMaxSmsOtp() { return maxSmsOtp; }
    public void setMaxSmsOtp(Integer maxSmsOtp) { this.maxSmsOtp = maxSmsOtp; }
}