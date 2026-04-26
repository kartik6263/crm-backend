package com.resolion.crm.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolion.crm.dpo.AiReportFilterPlan;
import com.resolion.crm.dpo.AiReportPlan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiReportService {

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> ALLOWED_REPORT_TYPES = List.of(
            "LEAD_ALL",
            "LEAD_STATUS",
            "LEAD_SOURCE",
            "SALES_METRICS",
            "INVOICE_SUMMARY",
            "INVOICE_ALL",
            "QUOTE_REPORT",
            "SALES_ORDER_REPORT",
            "PURCHASE_ORDER_REPORT",
            "CUSTOM"
    );

    private static final List<String> ALLOWED_FOLDERS = List.of(
            "All Reports",
            "My Reports",
            "Lead Reports",
            "Sales Metrics Reports",
            "Invoice Reports",
            "Quote Reports",
            "Sales Order Reports",
            "Purchase Order Reports"
    );

    private static final List<String> ALLOWED_FIELDS = List.of(
            "status",
            "source",
            "assignedTo",
            "createdDate",
            "invoiceDate",
            "quoteDate",
            "orderDate"
    );

    private static final List<String> ALLOWED_OPERATORS = List.of(
            "EQUALS",
            "CONTAINS"
    );

    public AiReportPlan generatePlan(String prompt) {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            return fallbackPlan(prompt);
        }

        try {
            URL url = new URL("https://api.openai.com/v1/responses");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + openAiApiKey);
            conn.setRequestProperty("Content-Type", "application/json");

            String requestBody = """
                    {
                      "model": "gpt-5.5-mini",
                      "input": [
                        {
                          "role": "system",
                          "content": "You convert CRM report requests into safe JSON only. Never write SQL. Allowed reportType values: LEAD_ALL, LEAD_STATUS, LEAD_SOURCE, SALES_METRICS, INVOICE_SUMMARY, INVOICE_ALL, QUOTE_REPORT, SALES_ORDER_REPORT, PURCHASE_ORDER_REPORT, CUSTOM. Allowed folders: My Reports, Lead Reports, Sales Metrics Reports, Invoice Reports, Quote Reports, Sales Order Reports, Purchase Order Reports. Return only JSON with reportName, description, folderName, reportType, filters."
                        },
                        {
                          "role": "user",
                          "content": "%s"
                        }
                      ]
                    }
                    """.formatted(escapeJson(prompt));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream is = conn.getInputStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String outputText = extractOutputText(response);
                AiReportPlan plan = objectMapper.readValue(outputText, AiReportPlan.class);
                return sanitizePlan(plan, prompt);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return fallbackPlan(prompt);
        }
    }

    private String extractOutputText(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode output = root.path("output");

        for (JsonNode item : output) {
            JsonNode content = item.path("content");
            for (JsonNode c : content) {
                if (c.has("text")) {
                    return c.get("text").asText();
                }
            }
        }

        throw new RuntimeException("No AI output text found");
    }

    private AiReportPlan sanitizePlan(AiReportPlan plan, String prompt) {
        if (plan == null) {
            return fallbackPlan(prompt);
        }

        if (plan.getReportName() == null || plan.getReportName().isBlank()) {
            plan.setReportName("AI Custom Report");
        }

        if (plan.getDescription() == null || plan.getDescription().isBlank()) {
            plan.setDescription("Generated from prompt: " + prompt);
        }

        if (!ALLOWED_REPORT_TYPES.contains(plan.getReportType())) {
            plan.setReportType("CUSTOM");
        }

        if (!ALLOWED_FOLDERS.contains(plan.getFolderName())) {
            plan.setFolderName("My Reports");
        }

        List<AiReportFilterPlan> safeFilters = new ArrayList<>();

        if (plan.getFilters() != null) {
            for (AiReportFilterPlan f : plan.getFilters()) {
                if (f == null) continue;

                if (!ALLOWED_FIELDS.contains(f.getFieldName())) continue;
                if (!ALLOWED_OPERATORS.contains(f.getOperator())) continue;

                safeFilters.add(f);
            }
        }

        plan.setFilters(safeFilters);
        return plan;
    }

    private AiReportPlan fallbackPlan(String prompt) {
        String p = prompt == null ? "" : prompt.toLowerCase();

        AiReportPlan plan = new AiReportPlan();
        plan.setDescription("Generated from prompt: " + prompt);
        plan.setFilters(new ArrayList<>());

        if (p.contains("invoice")) {
            plan.setReportName("AI Invoice Summary");
            plan.setFolderName("Invoice Reports");
            plan.setReportType("INVOICE_SUMMARY");
        } else if (p.contains("quote")) {
            plan.setReportName("AI Quote Report");
            plan.setFolderName("Quote Reports");
            plan.setReportType("QUOTE_REPORT");
        } else if (p.contains("source")) {
            plan.setReportName("AI Lead Source Report");
            plan.setFolderName("Lead Reports");
            plan.setReportType("LEAD_SOURCE");
        } else if (p.contains("status")) {
            plan.setReportName("AI Lead Status Report");
            plan.setFolderName("Lead Reports");
            plan.setReportType("LEAD_STATUS");
        } else if (p.contains("sales") || p.contains("conversion")) {
            plan.setReportName("AI Sales Metrics Report");
            plan.setFolderName("Sales Metrics Reports");
            plan.setReportType("SALES_METRICS");
        } else {
            plan.setReportName("AI Custom Report");
            plan.setFolderName("My Reports");
            plan.setReportType("CUSTOM");
        }

        return plan;
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
