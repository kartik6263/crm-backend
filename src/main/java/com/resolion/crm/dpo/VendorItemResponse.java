package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.VendorDiscountType;
import com.resolion.crm.ENUMS.VendorTaxType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorItemResponse {

    private Long id;
    private Integer serialNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal listPrice;
    private BigDecimal amount;

    private VendorDiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;

    private VendorTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    private BigDecimal total;
    private String description;
}