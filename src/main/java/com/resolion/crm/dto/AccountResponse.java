package com.resolion.crm.dto;

import com.resolion.crm.enums.AccountCopyAddressType;
import com.resolion.crm.enums.AccountIndustry;
import com.resolion.crm.enums.AccountOwnership;
import com.resolion.crm.enums.AccountRating;
import com.resolion.crm.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private Long id;

    private String accountOwner;
    private String ownerEmail;

    private AccountRating rating;

    private String accountSite;
    private String phone;
    private String accountName;
    private String fax;
    private String parentAccount;
    private String website;
    private String accountNumber;
    private String tickerSymbol;

    private AccountType accountType;
    private AccountOwnership ownership;
    private AccountIndustry industry;

    private String employees;
    private BigDecimal annualRevenue;
    private String sicCode;

    private AccountCopyAddressType copyAddress;

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

    private String description;

    private Long companyId;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}