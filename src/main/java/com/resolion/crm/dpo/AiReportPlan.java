package com.resolion.crm.dpo;

import java.util.List;

public class AiReportPlan {

    private String reportName;
    private String description;
    private String folderName;
    private String reportType;
    private List<AiReportFilterPlan> filters;

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFolderName() { return folderName; }
    public void setFolderName(String folderName) { this.folderName = folderName; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public List<AiReportFilterPlan> getFilters() { return filters; }
    public void setFilters(List<AiReportFilterPlan> filters) { this.filters = filters; }
}