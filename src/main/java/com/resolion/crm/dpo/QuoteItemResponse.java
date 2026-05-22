package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.QuoteDiscountType;
import com.resolion.crm.ENUMS.QuoteTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteItemResponse {

    private Long id;
    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;
    private BigDecimal amount;

    private QuoteDiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;

    private QuoteTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    private BigDecimal total;
    private String description;
}