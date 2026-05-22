package com.resolion.crm.entity;

import com.resolion.crm.enums.QuoteCarrier;
import com.resolion.crm.enums.QuoteCopyAddressType;
import com.resolion.crm.enums.QuoteStage;
import com.resolion.crm.enums.QuoteTaxType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
        name = "quotes",
        indexes = {
                @Index(name = "idx_quotes_company_id", columnList = "company_id"),
                @Index(name = "idx_quotes_owner_email", columnList = "owner_email"),
                @Index(name = "idx_quotes_created_by", columnList = "created_by"),
                @Index(name = "idx_quotes_stage", columnList = "quote_stage"),
                @Index(name = "idx_quotes_subject", columnList = "subject")
        }
)
public class QuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= QUOTE INFORMATION =================

    @Column(name = "quote_owner", length = 150)
    private String quoteOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "deal_name", length = 200)
    private String dealName;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "quote_stage", length = 80)
    private QuoteStage quoteStage;

    @Column(name = "contact_name", length = 150)
    private String contactName;

    @Column(name = "team", length = 150)
    private String team;

    @Column(name = "account_name", length = 200)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", length = 80)
    private QuoteCarrier carrier;

    // ================= ADDRESS =================

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_address", length = 80)
    private QuoteCopyAddressType copyAddress;

    @Column(name = "billing_street", length = 255)
    private String billingStreet;

    @Column(name = "shipping_street", length = 255)
    private String shippingStreet;

    @Column(name = "billing_city", length = 100)
    private String billingCity;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "billing_state", length = 100)
    private String billingState;

    @Column(name = "shipping_state", length = 100)
    private String shippingState;

    @Column(name = "billing_code", length = 30)
    private String billingCode;

    @Column(name = "shipping_code", length = 30)
    private String shippingCode;

    @Column(name = "billing_country", length = 100)
    private String billingCountry;

    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    // ================= TOTALS =================

    @Column(name = "sub_total", precision = 15, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "quote_discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "quote_tax_type", length = 50)
    private QuoteTaxType taxType;

    @Column(name = "quote_tax_rate", precision = 8, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "adjustment", precision = 15, scale = 2)
    private BigDecimal adjustment;

    @Column(name = "grand_total", precision = 15, scale = 2)
    private BigDecimal grandTotal;

    // ================= TERMS =================

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

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

    // ================= ITEMS =================

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuoteItemEntity> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdDate == null) {
            createdDate = now;
        }

        updatedDate = now;
        applyDefaults();
        applyCopyAddress();
        calculateTotals();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
        applyDefaults();
        applyCopyAddress();
        calculateTotals();
    }

    private void applyDefaults() {
        if (quoteStage == null) {
            quoteStage = QuoteStage.DRAFT;
        }

        if (carrier == null) {
            carrier = QuoteCarrier.NONE;
        }

        if (copyAddress == null) {
            copyAddress = QuoteCopyAddressType.NONE;
        }

        if (subTotal == null) {
            subTotal = BigDecimal.ZERO;
        }

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }

        if (taxType == null) {
            taxType = QuoteTaxType.NONE;
        }

        if (taxRate == null) {
            taxRate = BigDecimal.ZERO;
        }

        if (taxAmount == null) {
            taxAmount = BigDecimal.ZERO;
        }

        if (adjustment == null) {
            adjustment = BigDecimal.ZERO;
        }

        if (grandTotal == null) {
            grandTotal = BigDecimal.ZERO;
        }
    }

    private void applyCopyAddress() {
        if (copyAddress == QuoteCopyAddressType.BILLING_TO_SHIPPING) {
            shippingStreet = billingStreet;
            shippingCity = billingCity;
            shippingState = billingState;
            shippingCode = billingCode;
            shippingCountry = billingCountry;
        }

        if (copyAddress == QuoteCopyAddressType.SHIPPING_TO_BILLING) {
            billingStreet = shippingStreet;
            billingCity = shippingCity;
            billingState = shippingState;
            billingCode = shippingCode;
            billingCountry = shippingCountry;
        }
    }

    public void calculateTotals() {
        BigDecimal calculatedSubTotal = BigDecimal.ZERO;

        if (items != null) {
            for (QuoteItemEntity item : items) {
                item.calculateLineTotals();
                calculatedSubTotal = calculatedSubTotal.add(item.getTotal());
            }
        }

        subTotal = calculatedSubTotal.setScale(2, RoundingMode.HALF_UP);

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }

        if (adjustment == null) {
            adjustment = BigDecimal.ZERO;
        }

        if (taxRate == null) {
            taxRate = BigDecimal.ZERO;
        }

        BigDecimal taxableAmount = subTotal.subtract(discountAmount);

        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        if (taxType == null || taxType == QuoteTaxType.NONE) {
            taxAmount = BigDecimal.ZERO;
        } else {
            taxAmount = taxableAmount
                    .multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        grandTotal = taxableAmount
                .add(taxAmount)
                .add(adjustment)
                .setScale(2, RoundingMode.HALF_UP);
    }
}