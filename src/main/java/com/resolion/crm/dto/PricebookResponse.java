package com.resolion.crm.dto;

import com.resolion.crm.enums.PricebookPricingModel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricebookResponse {

    private Long id;

    private String pricebookOwner;
    private String ownerEmail;

    private String pricebookName;

    private Boolean active;

    private PricebookPricingModel pricingModel;

    private String description;

    private Long companyId;
    private String createdBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}