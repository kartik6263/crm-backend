package com.resolion.crm.dpo;

public class SalesLeaderboardResponse {

    private String email;
    private long assignedLeads;
    private long customers;
    private double conversionRate;

    public SalesLeaderboardResponse(String email, long assignedLeads, long customers, double conversionRate) {
        this.email = email;
        this.assignedLeads = assignedLeads;
        this.customers = customers;
        this.conversionRate = conversionRate;
    }

    public String getEmail() { return email; }
    public long getAssignedLeads() { return assignedLeads; }
    public long getCustomers() { return customers; }
    public double getConversionRate() { return conversionRate; }
}