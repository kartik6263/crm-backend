package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_recent_views")
public class ReportRecentView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String userEmail;
    private Long reportId;
    private String viewedDate;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getViewedDate() { return viewedDate; }
    public void setViewedDate(String viewedDate) { this.viewedDate = viewedDate; }
}