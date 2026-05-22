package com.resolion.crm.dpo;




import com.resolion.crm.ENUMS.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadRequest {

    private LeadSalutation salutation;

    private String ownerName;
    private String firstName;
    private String lastName;
    private String company;
    private String title;
    private String email;
    private Boolean optIn;
    private String phone;
    private String fax;
    private String mobile;
    private String website;

    private LeadSource source;
    private LeadStatus status;
    private LeadIndustry industry;
    private String employees;
    private String annualRevenue;
    private LeadRating rating;

    private String skypeId;
    private String secondaryEmail;

    private String twitter;
    private String facebook;
    private String instagram;
    private String linkedin;

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    private String description;
    private String assignedTo;
}
