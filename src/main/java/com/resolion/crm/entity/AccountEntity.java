package com.resolion.crm.entity;

import com.resolion.crm.enums.AccountCopyAddressType;
import com.resolion.crm.enums.AccountIndustry;
import com.resolion.crm.enums.AccountOwnership;
import com.resolion.crm.enums.AccountRating;
import com.resolion.crm.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "accounts",
        indexes = {
                @Index(name = "idx_accounts_company_id", columnList = "company_id"),
                @Index(name = "idx_accounts_owner_email", columnList = "owner_email"),
                @Index(name = "idx_accounts_created_by", columnList = "created_by"),
                @Index(name = "idx_accounts_name", columnList = "account_name"),
                @Index(name = "idx_accounts_phone", columnList = "phone")
        }
)
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= IMAGE =================

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_type")
    private String imageType;

    // ================= ACCOUNT INFORMATION =================

    @Column(name = "account_owner", length = 150)
    private String accountOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", length = 80)
    private AccountRating rating;

    @Column(name = "account_site", length = 150)
    private String accountSite;

    @Column(length = 30)
    private String phone;

    @Column(name = "account_name", nullable = false, length = 200)
    private String accountName;

    @Column(length = 30)
    private String fax;

    @Column(name = "parent_account", length = 200)
    private String parentAccount;

    @Column(length = 255)
    private String website;

    @Column(name = "account_number", length = 100)
    private String accountNumber;

    @Column(name = "ticker_symbol", length = 50)
    private String tickerSymbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", length = 80)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership", length = 80)
    private AccountOwnership ownership;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry", length = 120)
    private AccountIndustry industry;

    @Column(length = 50)
    private String employees;

    @Column(name = "annual_revenue", precision = 15, scale = 2)
    private BigDecimal annualRevenue;

    @Column(name = "sic_code", length = 50)
    private String sicCode;

    // ================= ADDRESS =================

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_address", length = 80)
    private AccountCopyAddressType copyAddress;

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

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdDate == null) {
            createdDate = now;
        }

        updatedDate = now;

        if (rating == null) {
            rating = AccountRating.NONE;
        }

        if (accountType == null) {
            accountType = AccountType.NONE;
        }

        if (ownership == null) {
            ownership = AccountOwnership.NONE;
        }

        if (industry == null) {
            industry = AccountIndustry.NONE;
        }

        if (copyAddress == null) {
            copyAddress = AccountCopyAddressType.NONE;
        }

        applyCopyAddress();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
        applyCopyAddress();
    }

    private void applyCopyAddress() {
        if (copyAddress == AccountCopyAddressType.BILLING_TO_SHIPPING) {
            shippingStreet = billingStreet;
            shippingCity = billingCity;
            shippingState = billingState;
            shippingCode = billingCode;
            shippingCountry = billingCountry;
        }

        if (copyAddress == AccountCopyAddressType.SHIPPING_TO_BILLING) {
            billingStreet = shippingStreet;
            billingCity = shippingCity;
            billingState = shippingState;
            billingCode = shippingCode;
            billingCountry = shippingCountry;
        }
    }
}