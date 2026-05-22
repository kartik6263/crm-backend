package com.resolion.crm.entity;

import com.resolion.crm.ENUMS.OfflineCampaignStatus;
import com.resolion.crm.ENUMS.OfflineCampaignType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "offline_campaigns",
        indexes = {
                @Index(name = "idx_offline_campaigns_company_id", columnList = "company_id"),
                @Index(name = "idx_offline_campaigns_owner_email", columnList = "owner_email"),
                @Index(name = "idx_offline_campaigns_created_by", columnList = "created_by"),
                @Index(name = "idx_offline_campaigns_type", columnList = "type"),
                @Index(name = "idx_offline_campaigns_status", columnList = "status"),
                @Index(name = "idx_offline_campaigns_campaign_name", columnList = "campaign_name"),
                @Index(name = "idx_offline_campaigns_start_date", columnList = "start_date")
        }
)
public class OfflineCampaignEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= CAMPAIGN INFO =================

    @Column(name = "campaign_owner", length = 150)
    private String campaignOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 80)
    private OfflineCampaignType type;

    @Column(name = "campaign_name", nullable = false, length = 255)
    private String campaignName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 80)
    private OfflineCampaignStatus status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

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

    // ================= RESPONSE METRICS =================

    @Column(name = "expected_response")
    private Integer expectedResponse;

    @Column(name = "numbers_sent")
    private Integer numbersSent;

    // expectedResponse / numbersSent * 100
    @Column(name = "expected_response_rate", precision = 10, scale = 2)
    private BigDecimal expectedResponseRate;

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
        calculateValues();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();

        applyDefaults();
        calculateValues();
    }

    private void applyDefaults() {
        if (type == null) {
            type = OfflineCampaignType.NONE;
        }

        if (status == null) {
            status = OfflineCampaignStatus.PLANNING;
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

        if (expectedResponse == null) {
            expectedResponse = 0;
        }

        if (numbersSent == null) {
            numbersSent = 0;
        }

        if (expectedResponseRate == null) {
            expectedResponseRate = BigDecimal.ZERO;
        }
    }

    public void calculateValues() {
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

        if (numbersSent != null && numbersSent > 0) {
            expectedResponseRate = BigDecimal.valueOf(expectedResponse == null ? 0 : expectedResponse)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(numbersSent), 2, RoundingMode.HALF_UP);
        } else {
            expectedResponseRate = BigDecimal.ZERO;
        }
    }
}