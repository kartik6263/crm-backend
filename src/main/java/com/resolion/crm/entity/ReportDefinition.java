package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_definitions")
public class ReportDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String reportName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String folderName;
    private String reportType;
    private String createdBy;
    private String createdDate;
    private String lastAccessedDate;
    private Boolean deleted = false;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFolderName() { return folderName; }
    public void setFolderName(String folderName) { this.folderName = folderName; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getLastAccessedDate() { return lastAccessedDate; }
    public void setLastAccessedDate(String lastAccessedDate) { this.lastAccessedDate = lastAccessedDate; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}