package com.resolion.crm.dto;

import com.resolion.crm.enums.SalesOrderCarrier;
import com.resolion.crm.enums.SalesOrderCopyAddressType;
import com.resolion.crm.enums.SalesOrderStatus;
import com.resolion.crm.enums.SalesOrderTaxType;
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
public class SalesOrderResponse {

    private Long id;

    private String salesOrderOwner;
    private String ownerEmail;

    private String dealName;
    private String subject;
    private String purchaseOrder;
    private String customerNo;
    private LocalDate dueDate;
    private String quoteName;
    private String contactName;
    private Boolean pending;

    private BigDecimal exciseDuty;

    private SalesOrderCarrier carrier;
    private SalesOrderStatus status;

    private BigDecimal salesCommission;

    private String accountName;

    private SalesOrderCopyAddressType copyAddress;

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
    private SalesOrderTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal adjustment;
    private BigDecimal grandTotal;

    private String termsAndConditions;
    private String description;

    private List<SalesOrderItemResponse> items;

    private Long companyId;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}