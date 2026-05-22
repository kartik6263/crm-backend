package com.resolion.crm.dto;

import com.resolion.crm.enums.SurveyCampaignType;
import com.resolion.crm.enums.SurveyDepartment;
import com.resolion.crm.enums.SurveyStatus;
import com.resolion.crm.enums.SurveyType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponse {

    private Long id;

    private SurveyDepartment surveyDepartment;
    private SurveyType surveyType;
    private String survey;

    private String campaignOwner;
    private String ownerEmail;

    private SurveyCampaignType type;

    private String campaignName;

    private SurveyStatus status;

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