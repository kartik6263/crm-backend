package com.resolion.crm.dpo;

import java.util.List;
import java.util.Map;

public class ReportRunResponse {

    private String reportName;
    private String reportType;
    private List<String> columns;
    private List<Map<String, Object>> rows;

    public ReportRunResponse(String reportName, String reportType, List<String> columns, List<Map<String, Object>> rows) {
        this.reportName = reportName;
        this.reportType = reportType;
        this.columns = columns;
        this.rows = rows;
    }

    public String getReportName() { return reportName; }
    public String getReportType() { return reportType; }
    public List<String> getColumns() { return columns; }
    public List<Map<String, Object>> getRows() { return rows; }
}