package com.resolion.crm.entity;

import com.resolion.crm.enums.SalesOrderCarrier;
import com.resolion.crm.enums.SalesOrderCopyAddressType;
import com.resolion.crm.enums.SalesOrderStatus;
import com.resolion.crm.enums.SalesOrderTaxType;
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
        name = "sales_orders",
        indexes = {
                @Index(name = "idx_sales_orders_company_id", columnList = "company_id"),
                @Index(name = "idx_sales_orders_owner_email", columnList = "owner_email"),
                @Index(name = "idx_sales_orders_created_by", columnList = "created_by"),
                @Index(name = "idx_sales_orders_subject", columnList = "subject"),
                @Index(name = "idx_sales_orders_status", columnList = "status")
        }
)
public class SalesOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= SALES ORDER INFO =================

    @Column(name = "sales_order_owner", length = 150)
    private String salesOrderOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "deal_name", length = 200)
    private String dealName;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "purchase_order", length = 200)
    private String purchaseOrder;

    @Column(name = "customer_no", length = 100)
    private String customerNo;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "quote_name", length = 200)
    private String quoteName;

    @Column(name = "contact_name", length = 150)
    private String contactName;

    @Column(name = "pending")
    private Boolean pending;

    @Column(name = "excise_duty", precision = 15, scale = 2)
    private BigDecimal exciseDuty;

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", length = 80)
    private SalesOrderCarrier carrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 80)
    private SalesOrderStatus status;

    @Column(name = "sales_commission", precision = 15, scale = 2)
    private BigDecimal salesCommission;

    @Column(name = "account_name", length = 200)
    private String accountName;

    // ================= ADDRESS =================

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_address", length = 80)
    private SalesOrderCopyAddressType copyAddress;

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

    @Column(name = "sales_order_discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_order_tax_type", length = 50)
    private SalesOrderTaxType taxType;

    @Column(name = "sales_order_tax_rate", precision = 8, scale = 2)
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

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalesOrderItemEntity> items = new ArrayList<>();

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
        if (pending == null) pending = false;
        if (carrier == null) carrier = SalesOrderCarrier.NONE;
        if (status == null) status = SalesOrderStatus.CREATED;
        if (copyAddress == null) copyAddress = SalesOrderCopyAddressType.NONE;

        if (subTotal == null) subTotal = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (taxType == null) taxType = SalesOrderTaxType.NONE;
        if (taxRate == null) taxRate = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (adjustment == null) adjustment = BigDecimal.ZERO;
        if (grandTotal == null) grandTotal = BigDecimal.ZERO;
        if (exciseDuty == null) exciseDuty = BigDecimal.ZERO;
        if (salesCommission == null) salesCommission = BigDecimal.ZERO;
    }

    private void applyCopyAddress() {
        if (copyAddress == SalesOrderCopyAddressType.BILLING_TO_SHIPPING) {
            shippingStreet = billingStreet;
            shippingCity = billingCity;
            shippingState = billingState;
            shippingCode = billingCode;
            shippingCountry = billingCountry;
        }

        if (copyAddress == SalesOrderCopyAddressType.SHIPPING_TO_BILLING) {
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
            for (SalesOrderItemEntity item : items) {
                item.calculateLineTotals();
                calculatedSubTotal = calculatedSubTotal.add(item.getTotal());
            }
        }

        subTotal = calculatedSubTotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxableAmount = subTotal.subtract(discountAmount);

        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        if (taxType == null || taxType == SalesOrderTaxType.NONE) {
            taxAmount = BigDecimal.ZERO;
        } else {
            taxAmount = taxableAmount
                    .multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        grandTotal = taxableAmount
                .add(taxAmount)
                .add(adjustment)
                .add(exciseDuty)
                .subtract(salesCommission)
                .setScale(2, RoundingMode.HALF_UP);
    }
}