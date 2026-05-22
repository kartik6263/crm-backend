package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.OfflineCampaignStatus;
import com.resolion.crm.ENUMS.OfflineCampaignType;
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