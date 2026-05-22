package com.resolion.crm.entity;

import com.resolion.crm.enums.PurchaseOrderCarrier;
import com.resolion.crm.enums.PurchaseOrderCopyAddressType;
import com.resolion.crm.enums.PurchaseOrderStatus;
import com.resolion.crm.enums.PurchaseOrderTaxType;
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
        name = "purchase_orders",
        indexes = {
                @Index(name = "idx_po_company_id", columnList = "company_id"),
                @Index(name = "idx_po_owner_email", columnList = "owner_email"),
                @Index(name = "idx_po_created_by", columnList = "created_by"),
                @Index(name = "idx_po_number", columnList = "po_number"),
                @Index(name = "idx_po_status", columnList = "status")
        }
)
public class PurchaseOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= PURCHASE ORDER INFO =================

    @Column(name = "purchase_order_owner", length = 150)
    private String purchaseOrderOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "po_number", nullable = false, length = 100)
    private String poNumber;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "vendor_name", length = 200)
    private String vendorName;

    @Column(name = "requisition_number", length = 100)
    private String requisitionNumber;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "contact_name", length = 150)
    private String contactName;

    @Column(name = "po_date")
    private LocalDate poDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", length = 80)
    private PurchaseOrderCarrier carrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 80)
    private PurchaseOrderStatus status;

    @Column(name = "excise_duty", precision = 15, scale = 2)
    private BigDecimal exciseDuty;

    @Column(name = "sales_commission", precision = 15, scale = 2)
    private BigDecimal salesCommission;

    // ================= ADDRESS =================

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_address", length = 80)
    private PurchaseOrderCopyAddressType copyAddress;

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

    @Column(name = "po_discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "po_tax_type", length = 50)
    private PurchaseOrderTaxType taxType;

    @Column(name = "po_tax_rate", precision = 8, scale = 2)
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

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseOrderItemEntity> items = new ArrayList<>();

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
        if (status == null) status = PurchaseOrderStatus.CREATED;
        if (carrier == null) carrier = PurchaseOrderCarrier.NONE;
        if (copyAddress == null) copyAddress = PurchaseOrderCopyAddressType.NONE;
        if (poDate == null) poDate = LocalDate.now();

        if (subTotal == null) subTotal = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (taxType == null) taxType = PurchaseOrderTaxType.NONE;
        if (taxRate == null) taxRate = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (adjustment == null) adjustment = BigDecimal.ZERO;
        if (grandTotal == null) grandTotal = BigDecimal.ZERO;
        if (exciseDuty == null) exciseDuty = BigDecimal.ZERO;
        if (salesCommission == null) salesCommission = BigDecimal.ZERO;
    }

    private void applyCopyAddress() {
        if (copyAddress == PurchaseOrderCopyAddressType.BILLING_TO_SHIPPING) {
            shippingStreet = billingStreet;
            shippingCity = billingCity;
            shippingState = billingState;
            shippingCode = billingCode;
            shippingCountry = billingCountry;
        }

        if (copyAddress == PurchaseOrderCopyAddressType.SHIPPING_TO_BILLING) {
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
            for (PurchaseOrderItemEntity item : items) {
                item.calculateLineTotals();
                calculatedSubTotal = calculatedSubTotal.add(item.getTotal());
            }
        }

        subTotal = calculatedSubTotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxableAmount = subTotal.subtract(discountAmount);

        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        if (taxType == null || taxType == PurchaseOrderTaxType.NONE) {
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