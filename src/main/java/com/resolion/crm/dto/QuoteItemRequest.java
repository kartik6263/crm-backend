package com.resolion.crm.dto;

import com.resolion.crm.enums.QuoteDiscountType;
import com.resolion.crm.enums.QuoteTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteItemRequest {

    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;

    private QuoteDiscountType discountType;
    private BigDecimal discountValue;

    private QuoteTaxType taxType;
    private BigDecimal taxRate;

    private String description;
}