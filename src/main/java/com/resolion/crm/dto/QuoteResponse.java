package com.resolion.crm.dto;

import com.resolion.crm.enums.QuoteCarrier;
import com.resolion.crm.enums.QuoteCopyAddressType;
import com.resolion.crm.enums.QuoteStage;
import com.resolion.crm.enums.QuoteTaxType;
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
public class QuoteResponse {

    private Long id;

    private String quoteOwner;
    private String ownerEmail;

    private String dealName;
    private String subject;
    private LocalDate validUntil;
    private QuoteStage quoteStage;
    private String contactName;
    private String team;
    private String accountName;
    private QuoteCarrier carrier;

    private QuoteCopyAddressType copyAddress;

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
    private QuoteTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal adjustment;
    private BigDecimal grandTotal;

    private String termsAndConditions;
    private String description;

    private List<QuoteItemResponse> items;

    private Long companyId;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}