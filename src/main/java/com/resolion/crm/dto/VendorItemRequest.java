package com.resolion.crm.dto;

import com.resolion.crm.enums.VendorDiscountType;
import com.resolion.crm.enums.VendorTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorItemRequest {

    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;

    private VendorDiscountType discountType;
    private BigDecimal discountValue;

    private VendorTaxType taxType;
    private BigDecimal taxRate;

    private String description;
}