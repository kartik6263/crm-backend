package com.resolion.crm.entity;

import com.resolion.crm.ENUMS.DealLeadSource;
import com.resolion.crm.ENUMS.DealStage;
import com.resolion.crm.ENUMS.DealType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "deals",
        indexes = {
                @Index(name = "idx_deals_company_id", columnList = "company_id"),
                @Index(name = "idx_deals_owner_email", columnList = "owner_email"),
                @Index(name = "idx_deals_stage", columnList = "stage"),
                @Index(name = "idx_deals_created_by", columnList = "created_by")
        }
)
public class DealEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= DEAL INFORMATION =================

    @Column(name = "owner_name", length = 150)
    private String ownerName;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "deal_name", nullable = false, length = 200)
    private String dealName;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "account_name", length = 200)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", length = 80)
    private DealStage stage;


    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    private DealType type;

    // Store probability as number: 0 to 100
    @Column(name = "probability")
    private Integer probability;

    @Column(name = "next_step", length = 255)
    private String nextStep;

    @Column(name = "expected_revenue", precision = 15, scale = 2)
    private BigDecimal expectedRevenue;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_source", length = 80)
    private DealLeadSource leadSource;

    @Column(name = "campaign_source", length = 150)
    private String campaignSource;

    @Column(name = "contact_name", length = 150)
    private String contactName;

    // ================= DESCRIPTION =================

    @Column(columnDefinition = "TEXT")
    private String description;

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

        if (stage == null) {
            stage = DealStage.QUALIFICATION;
        }

        if (type == null) {
            type = DealType.NONE;
        }

        if (leadSource == null) {
            leadSource = DealLeadSource.NONE;
        }

        if (probability == null) {
            probability = 0;
        }

        calculateExpectedRevenue();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
        calculateExpectedRevenue();
    }

    public void calculateExpectedRevenue() {
        if (amount == null || probability == null) {
            expectedRevenue = BigDecimal.ZERO;
            return;
        }

        expectedRevenue = amount
                .multiply(BigDecimal.valueOf(probability))
                .divide(BigDecimal.valueOf(100));
    }
}