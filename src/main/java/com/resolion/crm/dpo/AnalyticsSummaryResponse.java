package com.resolion.crm.dpo;

public class AnalyticsSummaryResponse {

    private long totalLeads;
    private long customers;
    private long lostLeads;
    private double conversionRate;

    private long totalInvoices;
    private long paidInvoices;
    private long unpaidInvoices;

    private long totalQuotes;
    private long acceptedQuotes;
    private long pendingQuotes;

    public long getTotalLeads() { return totalLeads; }
    public void setTotalLeads(long totalLeads) { this.totalLeads = totalLeads; }

    public long getCustomers() { return customers; }
    public void setCustomers(long customers) { this.customers = customers; }

    public long getLostLeads() { return lostLeads; }
    public void setLostLeads(long lostLeads) { this.lostLeads = lostLeads; }

    public double getConversionRate() { return conversionRate; }
    public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }

    public long getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; }

    public long getPaidInvoices() { return paidInvoices; }
    public void setPaidInvoices(long paidInvoices) { this.paidInvoices = paidInvoices; }

    public long getUnpaidInvoices() { return unpaidInvoices; }
    public void setUnpaidInvoices(long unpaidInvoices) { this.unpaidInvoices = unpaidInvoices; }

    public long getTotalQuotes() { return totalQuotes; }
    public void setTotalQuotes(long totalQuotes) { this.totalQuotes = totalQuotes; }

    public long getAcceptedQuotes() { return acceptedQuotes; }
    public void setAcceptedQuotes(long acceptedQuotes) { this.acceptedQuotes = acceptedQuotes; }

    public long getPendingQuotes() { return pendingQuotes; }
    public void setPendingQuotes(long pendingQuotes) { this.pendingQuotes = pendingQuotes; }
}