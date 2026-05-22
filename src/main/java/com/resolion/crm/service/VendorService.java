package com.resolion.crm.service;

import com.resolion.crm.enums.CompanyRole;
import com.resolion.crm.enums.VendorCategory;
import com.resolion.crm.enums.VendorGlAccount;
import com.resolion.crm.enums.VendorTaxType;
import com.resolion.crm.dto.*;
import com.resolion.crm.entity.VendorEntity;
import com.resolion.crm.entity.VendorItemEntity;
import com.resolion.crm.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public VendorResponse createVendor(String email, Long companyId, VendorRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        VendorEntity vendor = VendorEntity.builder()
                .vendorOwner(request.getVendorOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .vendorName(request.getVendorName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .website(normalizeUrl(request.getWebsite()))
                .glAccount(request.getGlAccount())
                .category(request.getCategory())
                .emailOptOut(request.getEmailOptOut())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .description(request.getDescription())
                .discountAmount(request.getDiscountAmount())
                .taxType(request.getTaxType())
                .taxRate(request.getTaxRate())
                .adjustment(request.getAdjustment())
                .companyId(companyId)
                .createdBy(email)
                .items(new ArrayList<>())
                .build();

        applyItems(vendor, request.getItems());
        vendor.calculateTotals();

        VendorEntity saved = vendorRepository.save(vendor);
        return toResponse(saved);
    }

    public List<VendorResponse> getVisibleVendors(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<VendorEntity> vendors;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            vendors = vendorRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            vendors = vendorRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            vendors = vendorRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return vendors.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public VendorResponse getVendorById(String email, Long id) {
        VendorEntity vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        validateAccess(email, vendor.getCompanyId());

        return toResponse(vendor);
    }

    public VendorResponse updateVendor(String email, Long id, VendorRequest request) {
        VendorEntity vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        validateAccess(email, vendor.getCompanyId());
        validateRequest(request);

        vendor.setVendorOwner(request.getVendorOwner());
        vendor.setOwnerEmail(request.getOwnerEmail());
        vendor.setVendorName(request.getVendorName());
        vendor.setPhone(request.getPhone());
        vendor.setEmail(request.getEmail());
        vendor.setWebsite(normalizeUrl(request.getWebsite()));
        vendor.setGlAccount(request.getGlAccount());
        vendor.setCategory(request.getCategory());
        vendor.setEmailOptOut(request.getEmailOptOut());
        vendor.setStreet(request.getStreet());
        vendor.setCity(request.getCity());
        vendor.setState(request.getState());
        vendor.setZipCode(request.getZipCode());
        vendor.setCountry(request.getCountry());
        vendor.setDescription(request.getDescription());
        vendor.setDiscountAmount(request.getDiscountAmount());
        vendor.setTaxType(request.getTaxType());
        vendor.setTaxRate(request.getTaxRate());
        vendor.setAdjustment(request.getAdjustment());

        vendor.getItems().clear();
        applyItems(vendor, request.getItems());

        vendor.calculateTotals();

        VendorEntity saved = vendorRepository.save(vendor);
        return toResponse(saved);
    }

    public void deleteVendor(String email, Long id) {
        VendorEntity vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        validateAccess(email, vendor.getCompanyId());

        vendorRepository.delete(vendor);
    }

    public List<VendorResponse> searchVendors(String email, Long companyId, String keyword) {
        validateAccess(email, companyId);

        return vendorRepository.searchByKeyword(companyId, keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<VendorResponse> getByGlAccount(String email, Long companyId, VendorGlAccount glAccount) {
        validateAccess(email, companyId);

        return vendorRepository.findByCompanyIdAndGlAccountOrderByIdDesc(companyId, glAccount)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<VendorResponse> getByCategory(String email, Long companyId, VendorCategory category) {
        validateAccess(email, companyId);

        return vendorRepository.findByCompanyIdAndCategoryOrderByIdDesc(companyId, category)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countVendors(String email, Long companyId) {
        validateAccess(email, companyId);
        return vendorRepository.countByCompanyId(companyId);
    }

    private void applyItems(VendorEntity vendor, List<VendorItemRequest> items) {
        if (items == null) {
            return;
        }

        int index = 1;

        for (VendorItemRequest itemRequest : items) {
            validateItem(itemRequest);

            VendorItemEntity item = VendorItemEntity.builder()
                    .serialNo(itemRequest.getSerialNo() == null ? index : itemRequest.getSerialNo())
                    .productId(itemRequest.getProductId())
                    .productName(itemRequest.getProductName())
                    .quantity(itemRequest.getQuantity())
                    .listPrice(itemRequest.getListPrice())
                    .discountType(itemRequest.getDiscountType())
                    .discountValue(itemRequest.getDiscountValue())
                    .taxType(itemRequest.getTaxType())
                    .taxRate(itemRequest.getTaxRate())
                    .description(itemRequest.getDescription())
                    .vendor(vendor)
                    .build();

            item.calculateLineTotals();
            vendor.getItems().add(item);
            index++;
        }
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(VendorRequest request) {
        if (request.getVendorName() == null || request.getVendorName().isBlank()) {
            throw new RuntimeException("Vendor name is required");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().contains("@")) {
            throw new RuntimeException("Invalid vendor email");
        }

        if (request.getDiscountAmount() != null && request.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Discount cannot be negative");
        }

        if (request.getTaxType() != null && request.getTaxType() != VendorTaxType.NONE) {
            if (request.getTaxRate() == null) {
                throw new RuntimeException("Tax rate is required");
            }

            if (request.getTaxRate().compareTo(BigDecimal.ZERO) < 0 ||
                    request.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new RuntimeException("Tax rate must be between 0 and 100");
            }
        }
    }

    private void validateItem(VendorItemRequest item) {
        if (item.getProductName() == null || item.getProductName().isBlank()) {
            throw new RuntimeException("Product name is required in vendor item");
        }

        if (item.getQuantity() == null || item.getQuantity() < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        if (item.getListPrice() == null || item.getListPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("List price is required and cannot be negative");
        }
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }

        String trimmed = url.trim();

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }

        return "https://" + trimmed;
    }

    private VendorResponse toResponse(VendorEntity vendor) {
        List<VendorItemResponse> itemResponses = vendor.getItems() == null
                ? List.of()
                : vendor.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return VendorResponse.builder()
                .id(vendor.getId())
                .vendorOwner(vendor.getVendorOwner())
                .ownerEmail(vendor.getOwnerEmail())
                .vendorName(vendor.getVendorName())
                .phone(vendor.getPhone())
                .email(vendor.getEmail())
                .website(vendor.getWebsite())
                .glAccount(vendor.getGlAccount())
                .category(vendor.getCategory())
                .emailOptOut(vendor.getEmailOptOut())
                .street(vendor.getStreet())
                .city(vendor.getCity())
                .state(vendor.getState())
                .zipCode(vendor.getZipCode())
                .country(vendor.getCountry())
                .description(vendor.getDescription())
                .subTotal(vendor.getSubTotal())
                .discountAmount(vendor.getDiscountAmount())
                .taxType(vendor.getTaxType())
                .taxRate(vendor.getTaxRate())
                .taxAmount(vendor.getTaxAmount())
                .adjustment(vendor.getAdjustment())
                .grandTotal(vendor.getGrandTotal())
                .items(itemResponses)
                .companyId(vendor.getCompanyId())
                .createdBy(vendor.getCreatedBy())
                .createdDate(vendor.getCreatedDate())
                .updatedDate(vendor.getUpdatedDate())
                .build();
    }

    private VendorItemResponse toItemResponse(VendorItemEntity item) {
        return VendorItemResponse.builder()
                .id(item.getId())
                .serialNo(item.getSerialNo())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .listPrice(item.getListPrice())
                .amount(item.getAmount())
                .discountType(item.getDiscountType())
                .discountValue(item.getDiscountValue())
                .discountAmount(item.getDiscountAmount())
                .taxType(item.getTaxType())
                .taxRate(item.getTaxRate())
                .taxAmount(item.getTaxAmount())
                .total(item.getTotal())
                .description(item.getDescription())
                .build();
    }
}