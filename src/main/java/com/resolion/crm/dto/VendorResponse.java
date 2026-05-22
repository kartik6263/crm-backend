package com.resolion.crm.dto;

import com.resolion.crm.enums.VendorCategory;
import com.resolion.crm.enums.VendorGlAccount;
import com.resolion.crm.enums.VendorTaxType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorResponse {

    private Long id;

    private String vendorOwner;
    private String ownerEmail;

    private String vendorName;
    private String phone;
    private String email;
    private String website;

    private VendorGlAccount glAccount;
    private VendorCategory category;

    private Boolean emailOptOut;

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    private String description;

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private VendorTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal adjustment;
    private BigDecimal grandTotal;

    private List<VendorItemResponse> items;

    private Long companyId;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}