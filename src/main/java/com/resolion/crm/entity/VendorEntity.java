package com.resolion.crm.entity;

import com.resolion.crm.ENUMS.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "vendors",
        indexes = {
                @Index(name = "idx_vendors_company_id", columnList = "company_id"),
                @Index(name = "idx_vendors_owner_email", columnList = "owner_email"),
                @Index(name = "idx_vendors_created_by", columnList = "created_by"),
                @Index(name = "idx_vendors_name", columnList = "vendor_name"),
                @Index(name = "idx_vendors_email", columnList = "email")
        }
)
public class VendorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= VENDOR INFO =================

    @Column(name = "vendor_owner", length = 150)
    private String vendorOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "vendor_name", nullable = false, length = 200)
    private String vendorName;

    @Column(length = 30)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(length = 255)
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "gl_account", length = 100)
    private VendorGlAccount glAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 100)
    private VendorCategory category;

    @Column(name = "email_opt_out")
    private Boolean emailOptOut;

    // ================= ADDRESS =================

    @Column(length = 255)
    private String street;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "zip_code", length = 30)
    private String zipCode;

    @Column(length = 100)
    private String country;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ================= TOTALS =================

    @Column(name = "sub_total", precision = 15, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", length = 50)
    private VendorTaxType taxType;

    @Column(name = "tax_rate", precision = 8, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "adjustment", precision = 15, scale = 2)
    private BigDecimal adjustment;

    @Column(name = "grand_total", precision = 15, scale = 2)
    private BigDecimal grandTotal;

    // ================= COMPANY / AUDIT =================

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "created_by", length = 150)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // ================= ITEMS =================

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VendorItemEntity> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdDate == null) {
            createdDate = now;
        }

        updatedDate = now;
        applyDefaults();
        calculateTotals();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
        applyDefaults();
        calculateTotals();
    }

    private void applyDefaults() {
        if (glAccount == null) glAccount = VendorGlAccount.NONE;
        if (category == null) category = VendorCategory.NONE;
        if (emailOptOut == null) emailOptOut = false;
        if (subTotal == null) subTotal = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (taxType == null) taxType = VendorTaxType.NONE;
        if (taxRate == null) taxRate = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (adjustment == null) adjustment = BigDecimal.ZERO;
        if (grandTotal == null) grandTotal = BigDecimal.ZERO;
    }

    public void calculateTotals() {
        BigDecimal calculatedSubTotal = BigDecimal.ZERO;

        if (items != null) {
            for (VendorItemEntity item : items) {
                item.calculateLineTotals();
                calculatedSubTotal = calculatedSubTotal.add(item.getTotal());
            }
        }

        subTotal = calculatedSubTotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxableAmount = subTotal.subtract(discountAmount == null ? BigDecimal.ZERO : discountAmount);

        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        if (taxType == null || taxType == VendorTaxType.NONE) {
            taxAmount = BigDecimal.ZERO;
        } else {
            taxAmount = taxableAmount
                    .multiply(taxRate == null ? BigDecimal.ZERO : taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        grandTotal = taxableAmount
                .add(taxAmount)
                .add(adjustment == null ? BigDecimal.ZERO : adjustment)
                .setScale(2, RoundingMode.HALF_UP);
    }
}