package com.resolion.crm.dto;

import com.resolion.crm.enums.OfflineCampaignStatus;
import com.resolion.crm.enums.OfflineCampaignType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfflineCampaignResponse {

    private Long id;

    private String campaignOwner;
    private String ownerEmail;

    private OfflineCampaignType type;

    private String campaignName;

    private OfflineCampaignStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal expectedRevenue;
    private BigDecimal budgetedCost;
    private BigDecimal actualCost;

    private BigDecimal expectedProfit;
    private BigDecimal costVariance;
    private BigDecimal roiPercentage;

    private Integer expectedResponse;
    private Integer numbersSent;
    private BigDecimal expectedResponseRate;

    private String description;

    private Long companyId;
    private String createdBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}