package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name="subscriptions")
public class Subscription {

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "company-id", insertable = false, updatable = false)
    private Long companyId;
    private String plan;
    private String status;
    private String paymentId;
    private String startDate;
    private String endDate;

    // ✅ ADD THIS
    public Long getId() { return id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}