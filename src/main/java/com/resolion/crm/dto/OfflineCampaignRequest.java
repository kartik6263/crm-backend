package com.resolion.crm.dto;

import com.resolion.crm.enums.OfflineCampaignStatus;
import com.resolion.crm.enums.OfflineCampaignType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfflineCampaignRequest {

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

    private Integer expectedResponse;
    private Integer numbersSent;

    private String description;
}