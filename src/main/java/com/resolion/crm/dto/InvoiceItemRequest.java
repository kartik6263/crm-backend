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
public class InvoiceItemRequest {

    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;

    private InvoiceDiscountType discountType;
    private BigDecimal discountValue;

    private InvoiceTaxType taxType;
    private BigDecimal taxRate;

    private String description;
}