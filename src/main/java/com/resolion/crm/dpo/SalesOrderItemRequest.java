package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.SalesOrderDiscountType;
import com.resolion.crm.ENUMS.SalesOrderTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderItemRequest {

    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;

    private SalesOrderDiscountType discountType;
    private BigDecimal discountValue;

    private SalesOrderTaxType taxType;
    private BigDecimal taxRate;

    private String description;
}