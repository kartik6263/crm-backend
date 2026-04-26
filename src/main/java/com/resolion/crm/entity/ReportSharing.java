package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_sharing")
public class ReportSharing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private Long reportId;
    private String sharedWithEmail;
    private String permission;

    // getters setters
    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getSharedWithEmail() { return sharedWithEmail; }
    public void setSharedWithEmail(String sharedWithEmail) { this.sharedWithEmail = sharedWithEmail; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
}
