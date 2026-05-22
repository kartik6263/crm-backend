package com.resolion.crm.dto;

import com.resolion.crm.enums.InvoiceCopyAddressType;
import com.resolion.crm.enums.InvoiceStatus;
import com.resolion.crm.enums.InvoiceTaxType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceRequest {

    private String invoiceOwner;
    private String ownerEmail;

    private String invoiceNumber;
    private String subject;

    private InvoiceStatus status;

    private LocalDate invoiceDate;
    private LocalDate dueDate;

    private String customerName;
    private String customerEmail;

    private String salesOrder;
    private String purchaseOrder;

    private BigDecimal exciseDuty;
    private BigDecimal salesCommission;

    private String accountName;
    private String contactName;
    private String dealName;

    private InvoiceCopyAddressType copyAddress;

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

    private BigDecimal discountAmount;
    private InvoiceTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal adjustment;
    private BigDecimal amountPaid;

    private String termsAndConditions;
    private String description;

    private List<InvoiceItemRequest> items;
}