package com.resolion.crm.entity;

import com.resolion.crm.enums.ProductCategory;
import com.resolion.crm.enums.ProductManufacturer;
import com.resolion.crm.enums.ProductTaxType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_products_company_id", columnList = "company_id"),
                @Index(name = "idx_products_owner_email", columnList = "owner_email"),
                @Index(name = "idx_products_created_by", columnList = "created_by"),
                @Index(name = "idx_products_name", columnList = "product_name"),
                @Index(name = "idx_products_code", columnList = "product_code"),
                @Index(name = "idx_products_active", columnList = "product_active")
        }
)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= IMAGE =================

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_type")
    private String imageType;

    // ================= PRODUCT INFORMATION =================

    @Column(name = "product_owner", length = 150)
    private String productOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_code", length = 100)
    private String productCode;

    @Column(name = "vendor_name", length = 200)
    private String vendorName;

    // Checkbox
    @Column(name = "product_active")
    private Boolean productActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "manufacturer", length = 80)
    private ProductManufacturer manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", length = 80)
    private ProductCategory productCategory;

    @Column(name = "sales_start_date")
    private LocalDate salesStartDate;

    @Column(name = "sales_end_date")
    private LocalDate salesEndDate;

    @Column(name = "support_start_date")
    private LocalDate supportStartDate;

    @Column(name = "support_end_date")
    private LocalDate supportEndDate;

    // Rs.
    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    // Percentage, example 10.5 means 10.5%
    @Column(name = "commission_rate", precision = 8, scale = 2)
    private BigDecimal commissionRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", length = 50)
    private ProductTaxType taxType;

    // Percentage, example 18.0 means 18%
    @Column(name = "tax_rate", precision = 8, scale = 2)
    private BigDecimal taxRate;

    // Checkbox
    @Column(name = "taxable")
    private Boolean taxable;

    @Column(name = "usage_unit", length = 50)
    private String usageUnit;

    @Column(name = "qty_ordered")
    private Integer qtyOrdered;

    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "handler", length = 150)
    private String handler;

    @Column(name = "quantity_in_demand")
    private Integer quantityInDemand;

    // ================= CALCULATED VALUES =================

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "commission_amount", precision = 15, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "available_stock")
    private Integer availableStock;

    @Column(name = "low_stock")
    private Boolean lowStock;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ================= COMPANY / AUDIT =================

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "created_by", length = 150)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdDate == null) {
            createdDate = now;
        }

        updatedDate = now;

        applyDefaults();
        calculateValues();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();

        applyDefaults();
        calculateValues();
    }

    private void applyDefaults() {
        if (productActive == null) {
            productActive = true;
        }

        if (manufacturer == null) {
            manufacturer = ProductManufacturer.NONE;
        }

        if (productCategory == null) {
            productCategory = ProductCategory.NONE;
        }

        if (taxType == null) {
            taxType = ProductTaxType.NONE;
        }

        if (taxable == null) {
            taxable = false;
        }

        if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO;
        }

        if (commissionRate == null) {
            commissionRate = BigDecimal.ZERO;
        }

        if (taxRate == null) {
            taxRate = BigDecimal.ZERO;
        }

        if (qtyOrdered == null) {
            qtyOrdered = 0;
        }

        if (quantityInStock == null) {
            quantityInStock = 0;
        }

        if (quantityInDemand == null) {
            quantityInDemand = 0;
        }

        if (reorderLevel == null) {
            reorderLevel = 0;
        }
    }

    public void calculateValues() {
        BigDecimal hundred = BigDecimal.valueOf(100);

        if (Boolean.TRUE.equals(taxable) && taxRate != null) {
            taxAmount = unitPrice
                    .multiply(taxRate)
                    .divide(hundred, 2, RoundingMode.HALF_UP);
        } else {
            taxAmount = BigDecimal.ZERO;
        }

        commissionAmount = unitPrice
                .multiply(commissionRate)
                .divide(hundred, 2, RoundingMode.HALF_UP);

        totalPrice = unitPrice.add(taxAmount);

        availableStock = quantityInStock - quantityInDemand;

        lowStock = availableStock <= reorderLevel;
    }
}