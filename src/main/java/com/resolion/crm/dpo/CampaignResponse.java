package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.CampaignStatus;
import com.resolion.crm.ENUMS.CampaignType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignResponse {

    private Long id;

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

    private BigDecimal expectedProfit;
    private BigDecimal costVariance;
    private BigDecimal roiPercentage;

    private String description;

    private Long companyId;
    private String createdBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}