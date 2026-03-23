package com.leadmatrix.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name="lead_activity")

public class LeadActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Long leadId;

    private String activityType;

    private String description;

    private String activityDate;

}