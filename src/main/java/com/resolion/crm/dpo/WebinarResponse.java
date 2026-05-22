package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.WebinarDurationType;
import com.resolion.crm.ENUMS.WebinarStatus;
import com.resolion.crm.ENUMS.WebinarType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebinarResponse {

    private Long id;

    private LocalDateTime webinarSchedule;

    private WebinarDurationType durationType;
    private Long durationMillis;
    private Long customDurationMillis;

    private String campaignOwner;
    private String ownerEmail;

    private WebinarType type;

    private String campaignName;

    private WebinarStatus status;

    private BigDecimal expectedRevenue;
    private BigDecimal budgetedCost;
    private BigDecimal actualCost;

    private BigDecimal expectedProfit;
    private BigDecimal costVariance;
    private BigDecimal roiPercentage;

    private String description;

    private Long companyId;
    private String createdBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}