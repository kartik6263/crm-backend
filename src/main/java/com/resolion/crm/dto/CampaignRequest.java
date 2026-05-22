package com.resolion.crm.dto;

import com.resolion.crm.enums.CampaignStatus;
import com.resolion.crm.enums.CampaignType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignRequest {

    private String campaignSubject;

    private String senderName;
    private String senderAddress;
    private String replyToAddress;

    private String campaignOwner;
    private String ownerEmail;

    private CampaignType type;

    private String campaignName;

    private CampaignStatus status;

    private BigDecimal expectedRevenue;
    private BigDecimal budgetedCost;
    private BigDecimal actualCost;

    private String description;
}