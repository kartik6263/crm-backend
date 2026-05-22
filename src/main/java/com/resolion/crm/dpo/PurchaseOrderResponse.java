package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.PurchaseOrderCarrier;
import com.resolion.crm.ENUMS.PurchaseOrderCopyAddressType;
import com.resolion.crm.ENUMS.PurchaseOrderStatus;
import com.resolion.crm.ENUMS.PurchaseOrderTaxType;
import com.resolion.crm.dpo.PurchaseOrderItemResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderResponse {

    private Long id;

    private String purchaseOrderOwner;
    private String ownerEmail;

    private String poNumber;
    private String subject;
    private String vendorName;
    private String requisitionNumber;
    private String trackingNumber;
    private String contactName;

    private LocalDate poDate;
    private LocalDate dueDate;

    private PurchaseOrderCarrier carrier;
    private PurchaseOrderStatus status;

    private BigDecimal exciseDuty;
    private BigDecimal salesCommission;

    private PurchaseOrderCopyAddressType copyAddress;

    private String billingStreet;
    private String shippingStreet;
    private String billingCity;
    private String shippingCity;
    private String billingState;
    private String shippingState;
    private String billingCode;
    private String shippingCode;
    private String billingCountry;
    private String shippingCountry;

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private PurchaseOrderTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal adjustment;
    private BigDecimal grandTotal;

    private String termsAndConditions;
    private String description;

    private List<PurchaseOrderItemResponse> items;

    private Long companyId;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}