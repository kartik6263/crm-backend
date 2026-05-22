package com.resolion.crm.dto;

import com.resolion.crm.enums.SalesOrderDiscountType;
import com.resolion.crm.enums.SalesOrderTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderItemResponse {

    private Long id;
    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;
    private BigDecimal amount;

    private SalesOrderDiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;

    private SalesOrderTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    private BigDecimal total;
    private String description;
}