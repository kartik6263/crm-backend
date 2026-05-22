package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.PricebookPricingModel;
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