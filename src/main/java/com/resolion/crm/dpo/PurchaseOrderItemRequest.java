package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.PurchaseOrderDiscountType;
import com.resolion.crm.ENUMS.PurchaseOrderTaxType;
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