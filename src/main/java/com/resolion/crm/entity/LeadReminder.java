package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name="lead_reminder")

public class LeadReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long leadId;
    private String reminderText;
    private String reminderDate;
    //private Long companyId;

    public Long getId() {
        return id;
    }


    public Long getLeadId() {
        return leadId;
    }
    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }


    public String getReminderText() {
        return reminderText;
    }
    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }


    public String getReminderDate() {
        return reminderDate;
    }
    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }


    /*public Long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }*/

}
