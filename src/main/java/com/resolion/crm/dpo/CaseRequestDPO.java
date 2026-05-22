package com.resolion.crm.dpo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.resolion.crm.ENUMS.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseRequestDPO {

    // ================= BASIC =================

    @NotBlank(message = "Subject is required")
    @Size(max = 255)
    private String subject;

    private CaseStatus status;

    /*
     * Optional because frontend may send null initially
     * Service layer will apply default value
     */
    private CaseType type;

    private CasePriority priority;

    private CaseOrigin caseOrigin;

    private CaseReason caseReason;

    // ================= RELATIONS =================

    private Long relatedLeadId;

    private Long relatedContactId;

    private Long relatedDealId;

    private Long relatedAccountId;

    private Long productId;

    private Long linkedSolutionId;

    // ================= CUSTOMER =================

    @Size(max = 255)
    private String customerName;

    @Size(max = 255)
    private String accountName;

    @Size(max = 255)
    private String dealName;

    @Size(max = 255)
    private String reportedBy;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20)
    private String phone;

    // ================= DETAILS =================

    @Size(max = 10000)
    private String description;

    @Size(max = 10000)
    private String internalComments;

    @Size(max = 10000)
    private String solution;

    // ================= ASSIGNMENT =================

    private String assignedTo;
}