package com.resolion.crm.dto;

import com.resolion.crm.enums.InvoiceDiscountType;
import com.resolion.crm.enums.InvoiceTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemResponse {

    private Long id;
    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;
    private BigDecimal amount;

    private InvoiceDiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;

    private InvoiceTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    private BigDecimal total;
    private String description;
}