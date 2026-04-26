package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company_usage")
public class CompanyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private Integer totalUsers = 0;
    private Integer totalLeads = 0;
    private Integer aiReportsUsed = 0;
    private Integer exportsUsed = 0;
    private Integer smsOtpUsed = 0;

    private String updatedDate;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Integer getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Integer totalUsers) { this.totalUsers = totalUsers; }

    public Integer getTotalLeads() { return totalLeads; }
    public void setTotalLeads(Integer totalLeads) { this.totalLeads = totalLeads; }

    public Integer getAiReportsUsed() { return aiReportsUsed; }
    public void setAiReportsUsed(Integer aiReportsUsed) { this.aiReportsUsed = aiReportsUsed; }

    public Integer getExportsUsed() { return exportsUsed; }
    public void setExportsUsed(Integer exportsUsed) { this.exportsUsed = exportsUsed; }

    public Integer getSmsOtpUsed() { return smsOtpUsed; }
    public void setSmsOtpUsed(Integer smsOtpUsed) { this.smsOtpUsed = smsOtpUsed; }

    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
}