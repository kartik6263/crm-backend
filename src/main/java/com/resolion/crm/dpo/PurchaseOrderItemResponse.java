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
public class PurchaseOrderItemResponse {

    private Long id;
    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;
    private BigDecimal amount;

    private PurchaseOrderDiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;

    private PurchaseOrderTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    private BigDecimal total;
    private String description;
}