package com.resolion.crm.dto;

import com.resolion.crm.enums.WebinarDurationType;
import com.resolion.crm.enums.WebinarStatus;
import com.resolion.crm.enums.WebinarType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebinarRequest {

    private LocalDateTime webinarSchedule;

    private WebinarDurationType durationType;

    // Required only when durationType = CUSTOM
    private Long customDurationMillis;

    private String campaignOwner;
    private String ownerEmail;

    private WebinarType type;

    private String campaignName;

    private WebinarStatus status;

    private BigDecimal expectedRevenue;
    private BigDecimal budgetedCost;
    private BigDecimal actualCost;

    private String description;
}