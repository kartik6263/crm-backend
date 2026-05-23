package com.resolion.crm.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryResponse {

    // LEADS
    private long totalLeads;
    private long customers;
    private long lostLeads;
    private double conversionRate;

    // INVOICES
    private long totalInvoices;
    private long paidInvoices;
    private long unpaidInvoices;

    // QUOTES
    private long totalQuotes;
    private long acceptedQuotes;
    private long pendingQuotes;
}