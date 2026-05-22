package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.ProductCategory;
import com.resolion.crm.ENUMS.ProductManufacturer;
import com.resolion.crm.ENUMS.ProductTaxType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    private String productOwner;
    private String ownerEmail;

    private String productName;
    private String productCode;
    private String vendorName;

    private Boolean productActive;

    private ProductManufacturer manufacturer;
    private ProductCategory productCategory;

    private LocalDate salesStartDate;
    private LocalDate salesEndDate;
    private LocalDate supportStartDate;
    private LocalDate supportEndDate;

    private BigDecimal unitPrice;
    private BigDecimal commissionRate;

    private ProductTaxType taxType;
    private BigDecimal taxRate;
    private Boolean taxable;

    private String usageUnit;

    private Integer qtyOrdered;
    private Integer quantityInStock;
    private Integer reorderLevel;
    private String handler;
    private Integer quantityInDemand;

    private String description;
}