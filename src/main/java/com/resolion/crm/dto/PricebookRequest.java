package com.resolion.crm.dto;

import com.resolion.crm.enums.PricebookPricingModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricebookRequest {

    private String pricebookOwner;
    private String ownerEmail;

    private String pricebookName;

    // Checkbox value: true / false
    private Boolean active;

    private PricebookPricingModel pricingModel;

    private String description;
}