package com.resolion.crm.dto;

import com.resolion.crm.enums.ContactCopyAddressType;
import com.resolion.crm.enums.ContactLeadSource;
import com.resolion.crm.enums.ContactSalutation;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponse {

    private Long id;

    private ContactSalutation salutation;

    private String ownerName;
    private String ownerEmail;

    private String firstName;
    private String lastName;
    private String fullName;

    private ContactLeadSource leadSource;

    private String accountName;
    private String title;
    private String email;
    private String department;
    private String phone;
    private String homePhone;
    private String otherPhone;
    private String fax;
    private String mobile;

    private LocalDate dateOfBirth;

    private String assistant;
    private String assistantPhone;
    private Boolean emailOptOut;

    private String skypeId;
    private String secondaryEmail;

    private String x;
    private String instagram;
    private String facebook;
    private String linkedIn;

    private String reportingTo;

    private String mailingStreet;
    private String otherStreet;
    private String mailingCity;
    private String otherCity;
    private String mailingState;
    private String otherState;
    private String mailingZip;
    private String otherZip;
    private String mailingCountry;
    private String otherCountry;

    private String description;

    private ContactCopyAddressType copyAddress;

    private Long companyId;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}