package com.resolion.crm.dto;

import com.resolion.crm.enums.PurchaseOrderDiscountType;
import com.resolion.crm.enums.PurchaseOrderTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItemRequest {

    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;

    private PurchaseOrderDiscountType discountType;
    private BigDecimal discountValue;

    private PurchaseOrderTaxType taxType;
    private BigDecimal taxRate;

    private String description;
}