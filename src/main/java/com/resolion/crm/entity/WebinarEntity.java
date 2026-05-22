package com.resolion.crm.entity;

import com.resolion.crm.ENUMS.WebinarDurationType;
import com.resolion.crm.ENUMS.WebinarStatus;
import com.resolion.crm.ENUMS.WebinarType;
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
        name = "webinars",
        indexes = {
                @Index(name = "idx_webinars_company_id", columnList = "company_id"),
                @Index(name = "idx_webinars_owner_email", columnList = "owner_email"),
                @Index(name = "idx_webinars_created_by", columnList = "created_by"),
                @Index(name = "idx_webinars_status", columnList = "status"),
                @Index(name = "idx_webinars_schedule", columnList = "webinar_schedule"),
                @Index(name = "idx_webinars_campaign_name", columnList = "campaign_name")
        }
)
public class WebinarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= WEBINAR / CAMPAIGN INFO =================

    @Column(name = "webinar_schedule", nullable = false)
    private LocalDateTime webinarSchedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_type", length = 80)
    private WebinarDurationType durationType;

    // milliseconds. Example: 1800000 = 30 minutes
    @Column(name = "duration_millis")
    private Long durationMillis;

    // only when durationType = CUSTOM
    @Column(name = "custom_duration_millis")
    private Long customDurationMillis;

    @Column(name = "campaign_owner", length = 150)
    private String campaignOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 80)
    private WebinarType type;

    @Column(name = "campaign_name", nullable = false, length = 255)
    private String campaignName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 80)
    private WebinarStatus status;

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

    // ((expectedRevenue - actualCost) / actualCost) * 100
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
        applyDuration();
        calculateFinancials();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();

        applyDefaults();
        applyDuration();
        calculateFinancials();
    }

    private void applyDefaults() {
        if (durationType == null) {
            durationType = WebinarDurationType.NONE;
        }

        if (type == null) {
            type = WebinarType.NONE;
        }

        if (status == null) {
            status = WebinarStatus.DRAFT;
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

    private void applyDuration() {
        if (durationType == WebinarDurationType.NONE) {
            durationMillis = 0L;
        } else if (durationType == WebinarDurationType.THIRTY_MINUTES) {
            durationMillis = 1800000L;
        } else if (durationType == WebinarDurationType.ONE_HOUR) {
            durationMillis = 3600000L;
        } else if (durationType == WebinarDurationType.ONE_HOUR_30_MINUTES) {
            durationMillis = 5400000L;
        } else if (durationType == WebinarDurationType.TWO_HOURS) {
            durationMillis = 7200000L;
        } else if (durationType == WebinarDurationType.TWO_HOURS_30_MINUTES) {
            durationMillis = 9000000L;
        } else if (durationType == WebinarDurationType.THREE_HOURS) {
            durationMillis = 10800000L;
        } else if (durationType == WebinarDurationType.CUSTOM) {
            durationMillis = customDurationMillis == null ? 0L : customDurationMillis;
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