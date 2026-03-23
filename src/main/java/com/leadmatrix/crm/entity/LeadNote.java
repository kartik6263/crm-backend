package com.leadmatrix.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name="lead_notes")

public class LeadNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Long leadId;

    private String noteText;

    private String createdBy;

    private String createdDate;

}
