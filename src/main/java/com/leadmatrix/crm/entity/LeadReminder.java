package com.leadmatrix.crm.entity;

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

}
