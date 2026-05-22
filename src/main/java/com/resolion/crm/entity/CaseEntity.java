package com.resolion.crm.entity;

import com.resolion.crm.ENUMS.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "cases",
        indexes = {
                @Index(name = "idx_case_company", columnList = "companyId"),
                @Index(name = "idx_case_status", columnList = "status"),
                @Index(name = "idx_case_priority", columnList = "priority"),
                @Index(name = "idx_case_assigned", columnList = "assignedTo")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ======================================================
    // AUTO GENERATED
    // ======================================================

    @Column(unique = true, nullable = false, length = 50)
    private String caseNumber;

    // ======================================================
    // COMPANY
    // ======================================================

    @Column(nullable = false)
    private Long companyId;

    // ======================================================
    // OWNERSHIP
    // ======================================================

    @Column(nullable = false)
    private String caseOwner;

    @Column(nullable = false)
    private String createdBy;

    private String assignedTo;

    // ======================================================
    // BASIC INFO
    // ======================================================

    @Column(nullable = false, length = 500)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CaseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CaseType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CasePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CaseOrigin caseOrigin;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private CaseReason caseReason;

    // ======================================================
    // RELATIONS
    // ======================================================

    private Long relatedLeadId;

    private Long relatedContactId;

    private Long relatedDealId;

    private Long relatedAccountId;

    private Long productId;

    private Long linkedSolutionId;

    // ======================================================
    // CUSTOMER INFO
    // ======================================================

    private String customerName;

    private String accountName;

    private String dealName;

    private String reportedBy;

    private String email;

    private String phone;

    // ======================================================
    // SUPPORT DETAILS
    // ======================================================

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String internalComments;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String solution;

    // ======================================================
    // SLA MANAGEMENT
    // ======================================================

    private String slaPolicy;

    private Integer responseTimeHours;

    private Integer resolutionTimeHours;

    private Boolean breached;

    private LocalDateTime firstResponseDue;

    private LocalDateTime resolutionDue;

    private LocalDateTime breachTime;

    private LocalDateTime resolvedAt;

    private LocalDateTime closedAt;

    // ======================================================
    // AI SUPPORT
    // ======================================================

    @Column(length = 5000)
    private String aiSummary;

    @Column(length = 5000)
    private String aiSuggestedSolution;

    private String aiCategory;

    private String aiSentiment;

    private Double aiConfidence;

    // ======================================================
    // CUSTOMER SATISFACTION
    // ======================================================

    private Integer customerSatisfactionRating;

    @Column(length = 5000)
    private String customerFeedback;

    // ======================================================
    // AUDIT
    // ======================================================

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    // ======================================================
    // AUTO EVENTS
    // ======================================================

    @PrePersist
    public void prePersist() {

        createdDate = LocalDateTime.now();

        updatedDate = LocalDateTime.now();

        if (status == null) {
            status = CaseStatus.NEW;
        }

        if (priority == null) {
            priority = CasePriority.MEDIUM;
        }

        if (breached == null) {
            breached = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}