package com.resolion.crm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.resolion.crm.ENUMS.VendorDiscountType;
import com.resolion.crm.ENUMS.VendorTaxType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vendor_items")
public class VendorItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_no")
    private Integer serialNo;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    private Integer quantity;

    @Column(name = "list_price", precision = 15, scale = 2)
    private BigDecimal listPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", length = 50)
    private VendorDiscountType discountType;

    @Column(name = "discount_value", precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", length = 50)
    private VendorTaxType taxType;

    @Column(name = "tax_rate", precision = 8, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal total;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    @JsonIgnore
    private VendorEntity vendor;

    public void calculateLineTotals() {
        if (quantity == null) quantity = 0;
        if (listPrice == null) listPrice = BigDecimal.ZERO;
        if (discountType == null) discountType = VendorDiscountType.NONE;
        if (discountValue == null) discountValue = BigDecimal.ZERO;
        if (taxType == null) taxType = VendorTaxType.NONE;
        if (taxRate == null) taxRate = BigDecimal.ZERO;

        amount = listPrice
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);

        if (discountType == VendorDiscountType.PERCENTAGE) {
            discountAmount = amount
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (discountType == VendorDiscountType.DIRECT_PRICE_REDUCTION) {
            discountAmount = discountValue;
        } else {
            discountAmount = BigDecimal.ZERO;
        }

        if (discountAmount.compareTo(amount) > 0) {
            discountAmount = amount;
        }

        BigDecimal taxableAmount = amount.subtract(discountAmount);

        if (taxType == VendorTaxType.NONE) {
            taxAmount = BigDecimal.ZERO;
        } else {
            taxAmount = taxableAmount
                    .multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        total = taxableAmount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
    }
}