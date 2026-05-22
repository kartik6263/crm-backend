package com.resolion.crm.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.resolion.crm.enums.ContactCopyAddressType;
import com.resolion.crm.enums.ContactLeadSource;
import com.resolion.crm.enums.ContactSalutation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "contacts",
        indexes = {
                @Index(name = "idx_contacts_company_id", columnList = "company_id"),
                @Index(name = "idx_contacts_owner_email", columnList = "owner_email"),
                @Index(name = "idx_contacts_created_by", columnList = "created_by"),
                @Index(name = "idx_contacts_email", columnList = "email"),
                @Index(name = "idx_contacts_phone", columnList = "phone")
        }
)
public class ContactsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= IMAGE =================

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_type")
    private String imageType;

    // ================= CONTACT INFORMATION =================

    @Enumerated(EnumType.STRING)
    @Column(name = "salutation", length = 50)
    private ContactSalutation salutation;

    @JsonAlias({"Ownername", "ownerName"})
    @Column(name = "owner_name", length = 150)
    private String ownerName;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @JsonAlias({"firstname", "firstName"})
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @JsonAlias({"lastname", "lastName"})
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_source", length = 80)
    private ContactLeadSource leadSource;

    @Column(name = "account_name", length = 200)
    private String accountName;

    @Column(length = 150)
    private String title;

    @Column(name = "email", length = 150)
    private String email;

    @Column(length = 150)
    private String department;

    @Column(length = 30)
    private String phone;

    @Column(name = "home_phone", length = 30)
    private String homePhone;

    @Column(name = "other_phone", length = 30)
    private String otherPhone;

    @Column(length = 30)
    private String fax;

    @Column(length = 30)
    private String mobile;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 150)
    private String assistant;

    @Column(name = "assistant_phone", length = 30)
    private String assistantPhone;

    @Column(name = "email_opt_out")
    private Boolean emailOptOut;

    @Column(name = "skype_id", length = 100)
    private String skypeId;

    @Column(name = "secondary_email", length = 150)
    private String secondaryEmail;

    // ================= SOCIAL LINKS =================

    @Column(name = "x_url", length = 255)
    private String x;

    @Column(length = 255)
    private String instagram;

    @Column(length = 255)
    private String facebook;

    @Column(name = "linkedin", length = 255)
    private String linkedIn;

    @Column(name = "reporting_to", length = 150)
    private String reportingTo;

    // ================= ADDRESS =================

    @Column(name = "mailing_street", length = 255)
    private String mailingStreet;

    @Column(name = "other_street", length = 255)
    private String otherStreet;

    @Column(name = "mailing_city", length = 100)
    private String mailingCity;

    @Column(name = "other_city", length = 100)
    private String otherCity;

    @Column(name = "mailing_state", length = 100)
    private String mailingState;

    @Column(name = "other_state", length = 100)
    private String otherState;

    @Column(name = "mailing_zip", length = 30)
    private String mailingZip;

    @Column(name = "other_zip", length = 30)
    private String otherZip;

    @Column(name = "mailing_country", length = 100)
    private String mailingCountry;

    @Column(name = "other_country", length = 100)
    private String otherCountry;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_address", length = 50)
    private ContactCopyAddressType copyAddress;

    // ================= COMPANY / AUDIT =================

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "created_by", length = 150)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdDate == null) {
            createdDate = now;
        }

        updatedDate = now;

        if (salutation == null) {
            salutation = ContactSalutation.NONE;
        }

        if (leadSource == null) {
            leadSource = ContactLeadSource.NONE;
        }

        if (copyAddress == null) {
            copyAddress = ContactCopyAddressType.NONE;
        }

        if (emailOptOut == null) {
            emailOptOut = false;
        }

        applyCopyAddress();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
        applyCopyAddress();
    }

    private void applyCopyAddress() {
        if (copyAddress == ContactCopyAddressType.MAILING_TO_OTHER) {
            otherStreet = mailingStreet;
            otherCity = mailingCity;
            otherState = mailingState;
            otherZip = mailingZip;
            otherCountry = mailingCountry;
        }

        if (copyAddress == ContactCopyAddressType.OTHER_TO_MAILING) {
            mailingStreet = otherStreet;
            mailingCity = otherCity;
            mailingState = otherState;
            mailingZip = otherZip;
            mailingCountry = otherCountry;
        }
    }

    @Transient
    public String getFullName() {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        return (first + " " + last).trim();
    }
}