package com.resolion.crm.services;

import com.resolion.crm.dpo.ReportRunResponse;
import com.resolion.crm.entity.LeadmatrixEntity;
import com.resolion.crm.entity.ReportDefinition;
import com.resolion.crm.respository.LeadmatrixRespository;
import com.resolion.crm.respository.ReportDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resolion.crm.entity.Invoice;
import com.resolion.crm.entity.Quote;
import com.resolion.crm.entity.SalesOrder;
import com.resolion.crm.entity.PurchaseOrder;
import com.resolion.crm.respository.InvoiceRepository;
import com.resolion.crm.respository.QuoteRepository;
import com.resolion.crm.respository.SalesOrderRepository;
import com.resolion.crm.respository.PurchaseOrderRepository;

import java.util.*;

@Service
public class ReportRunService {

    @Autowired
    private ReportDefinitionRepository reportDefinitionRepository;

    @Autowired
    private LeadmatrixRespository leadRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public ReportRunResponse runReport(Long reportId, Long companyId) {
        ReportDefinition report = reportDefinitionRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        String type = report.getReportType();

        if ("LEAD_ALL".equalsIgnoreCase(type)) {
            return allLeads(report, companyId);
        }

        if ("LEAD_STATUS".equalsIgnoreCase(type)) {
            return leadStatus(report, companyId);
        }

        if ("LEAD_SOURCE".equalsIgnoreCase(type)) {
            return leadSource(report, companyId);
        }

        if ("SALES_METRICS".equalsIgnoreCase(type)) {
            return salesMetrics(report, companyId);
        }


        if ("INVOICE_SUMMARY".equalsIgnoreCase(type)) {
            return invoiceSummary(report, companyId);
        }

        if ("INVOICE_ALL".equalsIgnoreCase(type)) {
            return allInvoices(report, companyId);
        }

        if ("QUOTE_REPORT".equalsIgnoreCase(type)) {
            return quoteSummary(report, companyId);
        }

        if ("SALES_ORDER_REPORT".equalsIgnoreCase(type)) {
            return salesOrderSummary(report, companyId);
        }

        if ("PURCHASE_ORDER_REPORT".equalsIgnoreCase(type)) {
            return purchaseOrderSummary(report, companyId);
        }

        return emptyReport(report);
    }


    private ReportRunResponse allInvoices(ReportDefinition report, Long companyId) {
        List<Invoice> invoices = invoiceRepository.findByCompanyIdOrderByIdDesc(companyId);

        List<String> columns = List.of("Invoice No", "Customer", "Email", "Amount", "Status", "Invoice Date", "Due Date");

        List<Map<String, Object>> rows = new ArrayList<>();

        for (Invoice i : invoices) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Invoice No", i.getInvoiceNumber());
            row.put("Customer", i.getCustomerName());
            row.put("Email", i.getCustomerEmail());
            row.put("Amount", i.getAmount());
            row.put("Status", i.getStatus());
            row.put("Invoice Date", i.getInvoiceDate());
            row.put("Due Date", i.getDueDate());
            rows.add(row);
        }

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private ReportRunResponse invoiceSummary(ReportDefinition report, Long companyId) {
        long total = invoiceRepository.countByCompanyId(companyId);
        long paid = invoiceRepository.countByCompanyIdAndStatus(companyId, "PAID");
        long unpaid = invoiceRepository.countByCompanyIdAndStatus(companyId, "UNPAID");
        long overdue = invoiceRepository.countByCompanyIdAndStatus(companyId, "OVERDUE");

        List<String> columns = List.of("Metric", "Value");
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("Metric", "Total Invoices", "Value", total));
        rows.add(row("Metric", "Paid Invoices", "Value", paid));
        rows.add(row("Metric", "Unpaid Invoices", "Value", unpaid));
        rows.add(row("Metric", "Overdue Invoices", "Value", overdue));

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private ReportRunResponse quoteSummary(ReportDefinition report, Long companyId) {
        long total = quoteRepository.countByCompanyId(companyId);
        long accepted = quoteRepository.countByCompanyIdAndStatus(companyId, "ACCEPTED");
        long pending = quoteRepository.countByCompanyIdAndStatus(companyId, "PENDING");
        long rejected = quoteRepository.countByCompanyIdAndStatus(companyId, "REJECTED");

        List<String> columns = List.of("Metric", "Value");
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("Metric", "Total Quotes", "Value", total));
        rows.add(row("Metric", "Accepted Quotes", "Value", accepted));
        rows.add(row("Metric", "Pending Quotes", "Value", pending));
        rows.add(row("Metric", "Rejected Quotes", "Value", rejected));

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private ReportRunResponse salesOrderSummary(ReportDefinition report, Long companyId) {
        long total = salesOrderRepository.countByCompanyId(companyId);
        long confirmed = salesOrderRepository.countByCompanyIdAndStatus(companyId, "CONFIRMED");
        long delivered = salesOrderRepository.countByCompanyIdAndStatus(companyId, "DELIVERED");
        long cancelled = salesOrderRepository.countByCompanyIdAndStatus(companyId, "CANCELLED");

        List<String> columns = List.of("Metric", "Value");
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("Metric", "Total Sales Orders", "Value", total));
        rows.add(row("Metric", "Confirmed Orders", "Value", confirmed));
        rows.add(row("Metric", "Delivered Orders", "Value", delivered));
        rows.add(row("Metric", "Cancelled Orders", "Value", cancelled));

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private ReportRunResponse purchaseOrderSummary(ReportDefinition report, Long companyId) {
        long total = purchaseOrderRepository.countByCompanyId(companyId);
        long ordered = purchaseOrderRepository.countByCompanyIdAndStatus(companyId, "ORDERED");
        long received = purchaseOrderRepository.countByCompanyIdAndStatus(companyId, "RECEIVED");
        long cancelled = purchaseOrderRepository.countByCompanyIdAndStatus(companyId, "CANCELLED");

        List<String> columns = List.of("Metric", "Value");
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("Metric", "Total Purchase Orders", "Value", total));
        rows.add(row("Metric", "Ordered", "Value", ordered));
        rows.add(row("Metric", "Received", "Value", received));
        rows.add(row("Metric", "Cancelled", "Value", cancelled));

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }








    private ReportRunResponse allLeads(ReportDefinition report, Long companyId) {
        List<LeadmatrixEntity> leads = leadRepository.findByCompanyId(companyId);

        List<String> columns = List.of("ID", "Name", "Email", "Phone", "Status", "Source", "Assigned To", "Created Date");

        List<Map<String, Object>> rows = new ArrayList<>();

        for (LeadmatrixEntity l : leads) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID", l.getId());
            row.put("Name", l.getName());
            row.put("Email", l.getEmail());
            row.put("Phone", l.getPhone());
            row.put("Status", l.getStatus());
            row.put("Source", l.getSource());
            row.put("Assigned To", l.getAssignedTo());
            row.put("Created Date", l.getCreatedDate());
            rows.add(row);
        }

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private ReportRunResponse leadStatus(ReportDefinition report, Long companyId) {
        List<String> statuses = List.of("NEW", "CONTACTED", "QUALIFIED", "CUSTOMER", "LOST");

        List<String> columns = List.of("Status", "Count");
        List<Map<String, Object>> rows = new ArrayList<>();

        for (String status : statuses) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Status", status);
            row.put("Count", leadRepository.countByCompanyIdAndStatus(companyId, status));
            rows.add(row);
        }

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private ReportRunResponse leadSource(ReportDefinition report, Long companyId) {
        List<String> sources = List.of("Facebook", "Website", "Referral");

        List<String> columns = List.of("Source", "Count");
        List<Map<String, Object>> rows = new ArrayList<>();

        for (String source : sources) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Source", source);
            row.put("Count", leadRepository.countByCompanyIdAndSource(companyId, source));
            rows.add(row);
        }

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private ReportRunResponse salesMetrics(ReportDefinition report, Long companyId) {
        long total = leadRepository.countByCompanyId(companyId);
        long customers = leadRepository.countByCompanyIdAndStatus(companyId, "CUSTOMER");
        long lost = leadRepository.countByCompanyIdAndStatus(companyId, "LOST");

        double conversionRate = total == 0 ? 0 : ((double) customers / total) * 100;

        List<String> columns = List.of("Metric", "Value");
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("Metric", "Total Leads", "Value", total));
        rows.add(row("Metric", "Customers", "Value", customers));
        rows.add(row("Metric", "Lost Leads", "Value", lost));
        rows.add(row("Metric", "Conversion Rate", "Value", String.format("%.2f%%", conversionRate)));

        return new ReportRunResponse(report.getReportName(), report.getReportType(), columns, rows);
    }

    private Map<String, Object> row(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put(k1, v1);
        row.put(k2, v2);
        return row;
    }

    private ReportRunResponse emptyReport(ReportDefinition report) {
        return new ReportRunResponse(
                report.getReportName(),
                report.getReportType(),
                List.of("Message"),
                List.of(Map.of("Message", "This report type is not connected yet."))
        );
    }
}