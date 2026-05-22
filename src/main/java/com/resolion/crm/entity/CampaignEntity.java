package com.resolion.crm.entity;

import com.resolion.crm.enums.CampaignStatus;
import com.resolion.crm.enums.CampaignType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "campaigns",
        indexes = {
                @Index(name = "idx_campaigns_company_id", columnList = "company_id"),
                @Index(name = "idx_campaigns_owner_email", columnList = "owner_email"),
                @Index(name = "idx_campaigns_created_by", columnList = "created_by"),
                @Index(name = "idx_campaigns_status", columnList = "status"),
                @Index(name = "idx_campaigns_type", columnList = "type"),
                @Index(name = "idx_campaigns_name", columnList = "campaign_name")
        }
)
public class CampaignEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= EMAIL / CAMPAIGN INFO =================

    @Column(name = "campaign_subject", nullable = false, length = 255)
    private String campaignSubject;

    @Column(name = "sender_name", length = 150)
    private String senderName;

    @Column(name = "sender_address", length = 150)
    private String senderAddress;

    @Column(name = "reply_to_address", length = 150)
    private String replyToAddress;

    @Column(name = "campaign_owner", length = 150)
    private String campaignOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 80)
    private CampaignType type;

    @Column(name = "campaign_name", nullable = false, length = 255)
    private String campaignName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 80)
    private CampaignStatus status;

    // ================= MONEY / CALCULATIONS =================

    @Column(name = "expected_revenue", precision = 15, scale = 2)
    private BigDecimal expectedRevenue;

    @Column(name = "budgeted_cost", precision = 15, scale = 2)
    private BigDecimal budgetedCost;

    @Column(name = "actual_cost", precision = 15, scale = 2)
    private BigDecimal actualCost;

    // expectedRevenue - actualCost
    @Column(name = "expected_profit", precision = 15, scale = 2)
    private BigDecimal expectedProfit;

    // actualCost - budgetedCost
    @Column(name = "cost_variance", precision = 15, scale = 2)
    private BigDecimal costVariance;

    // expectedProfit / actualCost * 100
    @Column(name = "roi_percentage", precision = 10, scale = 2)
    private BigDecimal roiPercentage;

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

        applyDefaults();
        calculateFinancials();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();

        applyDefaults();
        calculateFinancials();
    }

    private void applyDefaults() {
        if (type == null) {
            type = CampaignType.NONE;
        }

        if (status == null) {
            status = CampaignStatus.PLANNING;
        }

        if (expectedRevenue == null) {
            expectedRevenue = BigDecimal.ZERO;
        }

        if (budgetedCost == null) {
            budgetedCost = BigDecimal.ZERO;
        }

        if (actualCost == null) {
            actualCost = BigDecimal.ZERO;
        }

        if (expectedProfit == null) {
            expectedProfit = BigDecimal.ZERO;
        }

        if (costVariance == null) {
            costVariance = BigDecimal.ZERO;
        }

        if (roiPercentage == null) {
            roiPercentage = BigDecimal.ZERO;
        }
    }

    public void calculateFinancials() {
        expectedProfit = expectedRevenue
                .subtract(actualCost)
                .setScale(2, RoundingMode.HALF_UP);

        costVariance = actualCost
                .subtract(budgetedCost)
                .setScale(2, RoundingMode.HALF_UP);

        if (actualCost.compareTo(BigDecimal.ZERO) > 0) {
            roiPercentage = expectedProfit
                    .multiply(BigDecimal.valueOf(100))
                    .divide(actualCost, 2, RoundingMode.HALF_UP);
        } else {
            roiPercentage = BigDecimal.ZERO;
        }
    }
}