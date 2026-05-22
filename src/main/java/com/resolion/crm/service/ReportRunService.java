package com.resolion.crm.service;



import com.resolion.crm.dto.ReportRunResponse;
import com.resolion.crm.entity.InvoiceEntity;
import com.resolion.crm.entity.LeadmatrixEntity;
import com.resolion.crm.entity.PurchaseOrderEntity;
import com.resolion.crm.entity.QuoteEntity;
import com.resolion.crm.entity.ReportDefinition;
import com.resolion.crm.entity.SalesOrderEntity;
import com.resolion.crm.repository.InvoiceRepository;
import com.resolion.crm.repository.LeadmatrixRespository;
import com.resolion.crm.repository.PurchaseOrderRepository;
import com.resolion.crm.repository.QuoteRepository;
import com.resolion.crm.repository.ReportDefinitionRepository;
import com.resolion.crm.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportRunService {

    private final ReportDefinitionRepository reportDefinitionRepository;

    private final LeadmatrixRespository leadRepository;

    private final InvoiceRepository invoiceRepository;

    private final QuoteRepository quoteRepository;

    private final SalesOrderRepository salesOrderRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    // =========================================================
    // MAIN REPORT RUNNER
    // =========================================================

    public ReportRunResponse runReport(
            Long reportId,
            Long companyId
    ) {

        ReportDefinition report = reportDefinitionRepository
                .findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException("Report not found")
                );

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        String type = report.getReportType();

        if (type == null) {
            return emptyReport(report);
        }

        switch (type.toUpperCase()) {

            case "LEAD_ALL":
                return allLeads(report, companyId);

            case "LEAD_STATUS":
                return leadStatus(report, companyId);

            case "LEAD_SOURCE":
                return leadSource(report, companyId);

            case "SALES_METRICS":
                return salesMetrics(report, companyId);

            case "INVOICE_SUMMARY":
                return invoiceSummary(report, companyId);

            case "INVOICE_ALL":
                return allInvoices(report, companyId);

            case "QUOTE_REPORT":
                return quoteSummary(report, companyId);

            case "SALES_ORDER_REPORT":
                return salesOrderSummary(report, companyId);

            case "PURCHASE_ORDER_REPORT":
                return purchaseOrderSummary(report, companyId);

            default:
                return emptyReport(report);
        }
    }

    // =========================================================
    // INVOICE REPORTS
    // =========================================================

    private ReportRunResponse allInvoices(
            ReportDefinition report,
            Long companyId
    ) {

        List<InvoiceEntity> invoices =
                invoiceRepository.findByCompanyIdOrderByIdDesc(companyId);

        List<String> columns = List.of(
                "Invoice No",
                "Customer",
                "Email",
                "Amount",
                "Status",
                "Invoice Date",
                "Due Date"
        );

        List<Map<String, Object>> rows = new ArrayList<>();

        for (InvoiceEntity invoice : invoices) {

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("Invoice No", invoice.getInvoiceNumber());
            row.put("Customer", invoice.getCustomerName());
            row.put("Email", invoice.getCustomerEmail());
            row.put("Amount", invoice.getAmountPaid());
            row.put("Status", invoice.getStatus());
            row.put("Invoice Date", invoice.getInvoiceDate());
            row.put("Due Date", invoice.getDueDate());

            rows.add(row);
        }

        return response(report, columns, rows);
    }

    private ReportRunResponse invoiceSummary(
            ReportDefinition report,
            Long companyId
    ) {

        long total =
                invoiceRepository.countByCompanyId(companyId);

        long paid =
                invoiceRepository.countByCompanyIdAndStatus(
                        companyId,
                        "PAID"
                );

        long unpaid =
                invoiceRepository.countByCompanyIdAndStatus(
                        companyId,
                        "UNPAID"
                );

        long overdue =
                invoiceRepository.countByCompanyIdAndStatus(
                        companyId,
                        "OVERDUE"
                );

        List<String> columns = List.of("Metric", "Value");

        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(metric("Total Invoices", total));
        rows.add(metric("Paid Invoices", paid));
        rows.add(metric("Unpaid Invoices", unpaid));
        rows.add(metric("Overdue Invoices", overdue));

        return response(report, columns, rows);
    }

    // =========================================================
    // QUOTES
    // =========================================================

    private ReportRunResponse quoteSummary(
            ReportDefinition report,
            Long companyId
    ) {

        long total =
                quoteRepository.countByCompanyId(companyId);

        long accepted =
                quoteRepository.countByCompanyIdAndQuoteStage(
                        companyId,
                        "ACCEPTED"
                );

        long pending =
                quoteRepository.countByCompanyIdAndQuoteStage(
                        companyId,
                        "PENDING"
                );

        long rejected =
                quoteRepository.countByCompanyIdAndQuoteStage(
                        companyId,
                        "REJECTED"
                );

        List<String> columns = List.of("Metric", "Value");

        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(metric("Total Quotes", total));
        rows.add(metric("Accepted Quotes", accepted));
        rows.add(metric("Pending Quotes", pending));
        rows.add(metric("Rejected Quotes", rejected));

        return response(report, columns, rows);
    }

    // =========================================================
    // SALES ORDER
    // =========================================================

    private ReportRunResponse salesOrderSummary(
            ReportDefinition report,
            Long companyId
    ) {

        long total =
                salesOrderRepository.countByCompanyId(companyId);

        long confirmed =
                salesOrderRepository.countByCompanyIdAndStatus(
                        companyId,
                        "CONFIRMED"
                );

        long delivered =
                salesOrderRepository.countByCompanyIdAndStatus(
                        companyId,
                        "DELIVERED"
                );

        long cancelled =
                salesOrderRepository.countByCompanyIdAndStatus(
                        companyId,
                        "CANCELLED"
                );

        List<String> columns = List.of("Metric", "Value");

        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(metric("Total Sales Orders", total));
        rows.add(metric("Confirmed Orders", confirmed));
        rows.add(metric("Delivered Orders", delivered));
        rows.add(metric("Cancelled Orders", cancelled));

        return response(report, columns, rows);
    }

    // =========================================================
    // PURCHASE ORDER
    // =========================================================

    private ReportRunResponse purchaseOrderSummary(
            ReportDefinition report,
            Long companyId
    ) {

        long total =
                purchaseOrderRepository.countByCompanyId(companyId);

        long ordered =
                purchaseOrderRepository.countByCompanyIdAndStatus(
                        companyId,
                        "ORDERED"
                );

        long received =
                purchaseOrderRepository.countByCompanyIdAndStatus(
                        companyId,
                        "RECEIVED"
                );

        long cancelled =
                purchaseOrderRepository.countByCompanyIdAndStatus(
                        companyId,
                        "CANCELLED"
                );

        List<String> columns = List.of("Metric", "Value");

        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(metric("Total Purchase Orders", total));
        rows.add(metric("Ordered", ordered));
        rows.add(metric("Received", received));
        rows.add(metric("Cancelled", cancelled));

        return response(report, columns, rows);
    }

    // =========================================================
    // LEADS
    // =========================================================

    private ReportRunResponse allLeads(
            ReportDefinition report,
            Long companyId
    ) {

        List<LeadmatrixEntity> leads =
                leadRepository.findByCompanyId(companyId);

        List<String> columns = List.of(
                "ID",
                "Name",
                "Email",
                "Phone",
                "Status",
                "Source",
                "Assigned To",
                "Created Date"
        );

        List<Map<String, Object>> rows = new ArrayList<>();

        for (LeadmatrixEntity lead : leads) {

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("ID", lead.getId());
            row.put("Name", lead.getFirstName());
            row.put("Email", lead.getEmail());
            row.put("Phone", lead.getPhone());
            row.put("Status", lead.getStatus());
            row.put("Source", lead.getSource());
            row.put("Assigned To", lead.getAssignedTo());
            row.put("Created Date", lead.getCreatedDate());

            rows.add(row);
        }

        return response(report, columns, rows);
    }

    private ReportRunResponse leadStatus(
            ReportDefinition report,
            Long companyId
    ) {

        List<String> statuses = List.of(
                "NEW",
                "CONTACTED",
                "QUALIFIED",
                "CUSTOMER",
                "LOST"
        );

        List<String> columns = List.of(
                "Status",
                "Count"
        );

        List<Map<String, Object>> rows = new ArrayList<>();

        for (String status : statuses) {

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("Status", status);

            row.put(
                    "Count",
                    leadRepository.countByCompanyIdAndStatus(
                            companyId,
                            status
                    )
            );

            rows.add(row);
        }

        return response(report, columns, rows);
    }

    private ReportRunResponse leadSource(
            ReportDefinition report,
            Long companyId
    ) {

        List<String> sources = List.of(
                "Facebook",
                "Website",
                "Referral"
        );

        List<String> columns = List.of(
                "Source",
                "Count"
        );

        List<Map<String, Object>> rows = new ArrayList<>();

        for (String source : sources) {

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("Source", source);

            row.put(
                    "Count",
                    leadRepository.countByCompanyIdAndSource(
                            companyId,
                            source
                    )
            );

            rows.add(row);
        }

        return response(report, columns, rows);
    }

    private ReportRunResponse salesMetrics(
            ReportDefinition report,
            Long companyId
    ) {

        long total =
                leadRepository.countByCompanyId(companyId);

        long customers =
                leadRepository.countByCompanyIdAndStatus(
                        companyId,
                        "CUSTOMER"
                );

        long lost =
                leadRepository.countByCompanyIdAndStatus(
                        companyId,
                        "LOST"
                );

        double conversionRate =
                total == 0
                        ? 0
                        : ((double) customers / total) * 100;

        List<String> columns = List.of(
                "Metric",
                "Value"
        );

        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(metric("Total Leads", total));
        rows.add(metric("Customers", customers));
        rows.add(metric("Lost Leads", lost));
        rows.add(metric(
                "Conversion Rate",
                String.format("%.2f%%", conversionRate)
        ));

        return response(report, columns, rows);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Map<String, Object> metric(
            String metric,
            Object value
    ) {

        Map<String, Object> row =
                new LinkedHashMap<>();

        row.put("Metric", metric);

        row.put("Value", value);

        return row;
    }

    private ReportRunResponse response(
            ReportDefinition report,
            List<String> columns,
            List<Map<String, Object>> rows
    ) {

        return new ReportRunResponse(
                report.getReportName(),
                report.getReportType(),
                columns,
                rows
        );
    }

    private ReportRunResponse emptyReport(
            ReportDefinition report
    ) {

        return new ReportRunResponse(
                report.getReportName(),
                report.getReportType(),
                List.of("Message"),
                List.of(
                        Map.of(
                                "Message",
                                "This report type is not connected yet."
                        )
                )
        );
    }
}