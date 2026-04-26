package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_folders")
public class ReportFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String name;
    private Boolean visible = true;
    private Boolean systemFolder = true;

    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }

    public Boolean getSystemFolder() { return systemFolder; }
    public void setSystemFolder(Boolean systemFolder) { this.systemFolder = systemFolder; }
}