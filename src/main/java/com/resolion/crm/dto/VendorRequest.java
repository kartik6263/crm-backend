package com.resolion.crm.dto;

import com.resolion.crm.enums.VendorCategory;
import com.resolion.crm.enums.VendorGlAccount;
import com.resolion.crm.enums.VendorTaxType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorRequest {

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

    private BigDecimal discountAmount;
    private VendorTaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal adjustment;

    private List<VendorItemRequest> items;
}
