package com.resolion.crm.entity;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.resolion.crm.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Data  // using lombok to handle
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "leads",
        indexes = {
                @Index(name = "idx_leads_company_id", columnList = "company_id"),
                @Index(name = "idx_leads_assigned_to", columnList = "assigned_to"),
                @Index(name = "idx_leads_created_by", columnList = "created_by"),
                @Index(name = "idx_leads_status", columnList = "status"),
                @Index(name = "idx_leads_source", columnList = "source")
        }
)
public class LeadmatrixEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= IMAGE =================


    // 1. store the actual image data in the db
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;
    //@Column(name = "image_data", columnDefinition = "LONGBLOB", length = 1000)

    //2. Store only the file name or url
    @Column(name = "image_name")
    private String imageName;
    @Column(name = "image_type")
    private String imageType;

//================================ Salutation =====================================
    @Enumerated(EnumType.STRING)
    @Column(name = "salutation", length = 50)
    private LeadSalutation salutation;
    // ================= LEAD INFORMATION =================


    @JsonAlias({"Ownername", "ownerName"})
    @Column(name = "owner_name", length = 150)
    private String ownerName;

    @JsonAlias({"firstname", "firstName"})
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @JsonAlias({"lastname", "lastName"})
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 150)
    private String company;

    @Column(length = 150)
    private String title;

    @Column(length = 150)
    private String email;


    // the opt-in field
    @Column(name = "opt_in")
    private boolean optIn = false;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 50)
    private String fax;

    @Column(length = 30)
    private String mobile;

    @Column(length = 255)
    private String website;


    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 80)
    private LeadSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 80)
    private LeadStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry", length = 120)
    private LeadIndustry industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", length = 80)
    private LeadRating rating;

    @Column(length = 50)
    private String employees;



    @Column(name = "annual_revenue", length = 100)
    private String annualRevenue;

    @Column(name = "skype_id", length = 100)
    private String skypeId;

    @Column(name = "secondary_email", length = 150)
    private String secondaryEmail;


    private String twitter;
    private String facebook;
    private String instagram;
    private String linkedin;

    // ================= ADDRESS =================

    @Column(length = 255)
    private String street;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @JsonAlias({"zipCoad", "zipCode"})
    @Column(name = "zip_code", length = 30)
    private String zipCode;

    @Column(length = 100)
    private String country;


    // ================= DESCRIPTION =================

    @Column(columnDefinition = "TEXT")
    private String description;

    // ================= CRM META =================

    @Column(name = "assigned_to", length = 150)
    private String assignedTo;

    @Column(length = 255)
    private String document;

    @Column(name = "created_by", length = 150)
    private String createdBy;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "updated_date")
    private String updatedDate;

    private Integer score = 0;

    @Column(name = "last_activity")
    private String lastActivity;

    // ================= AUTO DATES =================

    @PrePersist
    public void prePersist() {
        String now = LocalDateTime.now().toString();

        if (createdDate == null || createdDate.isBlank()) {
            createdDate = now;
        }

        //updatedDate = now;

//        if (status == null || status.isBlank()) {
//            status = "NEW";
//        }
//
//        if (source == null || source.isBlank()) {
//            source = "Unknown";
//        }

        if (score == null) {
            score = 0;
        }

        if (salutation == null) {
            salutation = LeadSalutation.NONE;
        }

        if (status == null) {
            status = LeadStatus.NEW;
        }

        if (source == null) {
            source = LeadSource.NONE;
        }

        if (industry == null) {
            industry = LeadIndustry.NONE;
        }

        if (rating == null) {
            rating = LeadRating.NONE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now().toString();
    }

    // ================= HELPER =================

    @Transient
    public String getFullName() {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        return (first + " " + last).trim();
    }


    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

}

