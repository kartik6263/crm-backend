package com.resolion.crm.dpo;


import com.resolion.crm.ENUMS.DealLeadSource;
import com.resolion.crm.ENUMS.DealStage;
import com.resolion.crm.ENUMS.DealType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealResponse {

    private Long id;

    private String ownerName;
    private String ownerEmail;

    private String dealName;
    private BigDecimal amount;
    private LocalDate closingDate;

    private String accountName;
    private Integer probability;
    private String nextStep;

    private BigDecimal expectedRevenue;

    private DealStage stage;
    private DealType type;
    private DealLeadSource leadSource;

    private String campaignSource;
    private String contactName;

    private String description;

    private Long companyId;
    private String createdBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}