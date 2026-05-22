package com.resolion.crm.dto;

import com.resolion.crm.enums.DealLeadSource;
import com.resolion.crm.enums.DealStage;
import com.resolion.crm.enums.DealType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealRequest {

    private String ownerName;
    private String ownerEmail;

    private String dealName;
    private BigDecimal amount;
    private LocalDate closingDate;

    private String accountName;
    private Integer probability;
    private String nextStep;

    private DealStage stage;
    private DealType type;
    private DealLeadSource leadSource;
    private String campaignSource;
    private String contactName;

    private String description;
}