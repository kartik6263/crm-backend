package com.resolion.crm.service;

import com.resolion.crm.enums.CompanyRole;
import com.resolion.crm.enums.PurchaseOrderStatus;
import com.resolion.crm.enums.PurchaseOrderTaxType;
import com.resolion.crm.dto.*;
import com.resolion.crm.entity.PurchaseOrderEntity;
import com.resolion.crm.entity.PurchaseOrderItemEntity;
import com.resolion.crm.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public PurchaseOrderResponse createPurchaseOrder(String email, Long companyId, PurchaseOrderRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        if (purchaseOrderRepository.existsByCompanyIdAndPoNumberIgnoreCase(companyId, request.getPoNumber())) {
            throw new RuntimeException("Purchase order number already exists");
        }

        PurchaseOrderEntity po = PurchaseOrderEntity.builder()
                .purchaseOrderOwner(request.getPurchaseOrderOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .poNumber(request.getPoNumber())
                .subject(request.getSubject())
                .vendorName(request.getVendorName())
                .requisitionNumber(request.getRequisitionNumber())
                .trackingNumber(request.getTrackingNumber())
                .contactName(request.getContactName())
                .poDate(request.getPoDate())
                .dueDate(request.getDueDate())
                .carrier(request.getCarrier())
                .status(request.getStatus())
                .exciseDuty(request.getExciseDuty())
                .salesCommission(request.getSalesCommission())
                .copyAddress(request.getCopyAddress())
                .billingStreet(request.getBillingStreet())
                .shippingStreet(request.getShippingStreet())
                .billingCity(request.getBillingCity())
                .shippingCity(request.getShippingCity())
                .billingState(request.getBillingState())
                .shippingState(request.getShippingState())
                .billingCode(request.getBillingCode())
                .shippingCode(request.getShippingCode())
                .billingCountry(request.getBillingCountry())
                .shippingCountry(request.getShippingCountry())
                .discountAmount(request.getDiscountAmount())
                .taxType(request.getTaxType())
                .taxRate(request.getTaxRate())
                .adjustment(request.getAdjustment())
                .termsAndConditions(request.getTermsAndConditions())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .items(new ArrayList<>())
                .build();

        applyItems(po, request.getItems());
        po.calculateTotals();

        PurchaseOrderEntity saved = purchaseOrderRepository.save(po);
        return toResponse(saved);
    }

    public List<PurchaseOrderResponse> getVisiblePurchaseOrders(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<PurchaseOrderEntity> orders;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            orders = purchaseOrderRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            orders = purchaseOrderRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            orders = purchaseOrderRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return orders.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PurchaseOrderResponse getPurchaseOrderById(String email, Long id) {
        PurchaseOrderEntity po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        validateAccess(email, po.getCompanyId());

        return toResponse(po);
    }

    public PurchaseOrderResponse updatePurchaseOrder(String email, Long id, PurchaseOrderRequest request) {
        PurchaseOrderEntity po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        validateAccess(email, po.getCompanyId());
        validateRequest(request);

        purchaseOrderRepository.findByCompanyIdAndPoNumberIgnoreCase(
                po.getCompanyId(),
                request.getPoNumber()
        ).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Purchase order number already exists");
            }
        });

        po.setPurchaseOrderOwner(request.getPurchaseOrderOwner());
        po.setOwnerEmail(request.getOwnerEmail());
        po.setPoNumber(request.getPoNumber());
        po.setSubject(request.getSubject());
        po.setVendorName(request.getVendorName());
        po.setRequisitionNumber(request.getRequisitionNumber());
        po.setTrackingNumber(request.getTrackingNumber());
        po.setContactName(request.getContactName());
        po.setPoDate(request.getPoDate());
        po.setDueDate(request.getDueDate());
        po.setCarrier(request.getCarrier());
        po.setStatus(request.getStatus());
        po.setExciseDuty(request.getExciseDuty());
        po.setSalesCommission(request.getSalesCommission());
        po.setCopyAddress(request.getCopyAddress());
        po.setBillingStreet(request.getBillingStreet());
        po.setShippingStreet(request.getShippingStreet());
        po.setBillingCity(request.getBillingCity());
        po.setShippingCity(request.getShippingCity());
        po.setBillingState(request.getBillingState());
        po.setShippingState(request.getShippingState());
        po.setBillingCode(request.getBillingCode());
        po.setShippingCode(request.getShippingCode());
        po.setBillingCountry(request.getBillingCountry());
        po.setShippingCountry(request.getShippingCountry());
        po.setDiscountAmount(request.getDiscountAmount());
        po.setTaxType(request.getTaxType());
        po.setTaxRate(request.getTaxRate());
        po.setAdjustment(request.getAdjustment());
        po.setTermsAndConditions(request.getTermsAndConditions());
        po.setDescription(request.getDescription());

        po.getItems().clear();
        applyItems(po, request.getItems());

        po.calculateTotals();

        PurchaseOrderEntity saved = purchaseOrderRepository.save(po);
        return toResponse(saved);
    }

    public PurchaseOrderResponse updateStatus(String email, Long id, PurchaseOrderStatus status) {
        PurchaseOrderEntity po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        validateAccess(email, po.getCompanyId());

        po.setStatus(status);

        PurchaseOrderEntity saved = purchaseOrderRepository.save(po);
        return toResponse(saved);
    }

    public void deletePurchaseOrder(String email, Long id) {
        PurchaseOrderEntity po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        validateAccess(email, po.getCompanyId());

        purchaseOrderRepository.delete(po);
    }

    public List<PurchaseOrderResponse> getByStatus(String email, Long companyId, PurchaseOrderStatus status) {
        validateAccess(email, companyId);

        return purchaseOrderRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void applyItems(PurchaseOrderEntity po, List<PurchaseOrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("At least one purchase order item is required");
        }

        int index = 1;

        for (PurchaseOrderItemRequest itemRequest : items) {
            validateItem(itemRequest);

            PurchaseOrderItemEntity item = PurchaseOrderItemEntity.builder()
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
                    .purchaseOrder(po)
                    .build();

            item.calculateLineTotals();
            po.getItems().add(item);
            index++;
        }
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(PurchaseOrderRequest request) {
        if (request.getPoNumber() == null || request.getPoNumber().isBlank()) {
            throw new RuntimeException("Purchase order number is required");
        }

        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new RuntimeException("Subject is required");
        }

        if (request.getDueDate() != null &&
                request.getPoDate() != null &&
                request.getDueDate().isBefore(request.getPoDate())) {
            throw new RuntimeException("Due date cannot be before PO date");
        }

        if (request.getDiscountAmount() != null && request.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Discount cannot be negative");
        }

        if (request.getTaxType() != null && request.getTaxType() != PurchaseOrderTaxType.NONE) {
            if (request.getTaxRate() == null) {
                throw new RuntimeException("Tax rate is required");
            }

            if (request.getTaxRate().compareTo(BigDecimal.ZERO) < 0 ||
                    request.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new RuntimeException("Tax rate must be between 0 and 100");
            }
        }
    }

    private void validateItem(PurchaseOrderItemRequest item) {
        if (item.getProductName() == null || item.getProductName().isBlank()) {
            throw new RuntimeException("Product name is required in purchase order item");
        }

        if (item.getQuantity() == null || item.getQuantity() < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        if (item.getListPrice() == null || item.getListPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("List price is required and cannot be negative");
        }

        if (item.getDiscountValue() != null && item.getDiscountValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Discount cannot be negative");
        }

        if (item.getTaxRate() != null &&
                (item.getTaxRate().compareTo(BigDecimal.ZERO) < 0 ||
                        item.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new RuntimeException("Item tax rate must be between 0 and 100");
        }
    }

    private PurchaseOrderResponse toResponse(PurchaseOrderEntity po) {
        List<PurchaseOrderItemResponse> itemResponses = po.getItems() == null
                ? List.of()
                : po.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .purchaseOrderOwner(po.getPurchaseOrderOwner())
                .ownerEmail(po.getOwnerEmail())
                .poNumber(po.getPoNumber())
                .subject(po.getSubject())
                .vendorName(po.getVendorName())
                .requisitionNumber(po.getRequisitionNumber())
                .trackingNumber(po.getTrackingNumber())
                .contactName(po.getContactName())
                .poDate(po.getPoDate())
                .dueDate(po.getDueDate())
                .carrier(po.getCarrier())
                .status(po.getStatus())
                .exciseDuty(po.getExciseDuty())
                .salesCommission(po.getSalesCommission())
                .copyAddress(po.getCopyAddress())
                .billingStreet(po.getBillingStreet())
                .shippingStreet(po.getShippingStreet())
                .billingCity(po.getBillingCity())
                .shippingCity(po.getShippingCity())
                .billingState(po.getBillingState())
                .shippingState(po.getShippingState())
                .billingCode(po.getBillingCode())
                .shippingCode(po.getShippingCode())
                .billingCountry(po.getBillingCountry())
                .shippingCountry(po.getShippingCountry())
                .subTotal(po.getSubTotal())
                .discountAmount(po.getDiscountAmount())
                .taxType(po.getTaxType())
                .taxRate(po.getTaxRate())
                .taxAmount(po.getTaxAmount())
                .adjustment(po.getAdjustment())
                .grandTotal(po.getGrandTotal())
                .termsAndConditions(po.getTermsAndConditions())
                .description(po.getDescription())
                .items(itemResponses)
                .companyId(po.getCompanyId())
                .createdBy(po.getCreatedBy())
                .createdDate(po.getCreatedDate())
                .updatedDate(po.getUpdatedDate())
                .build();
    }

    private PurchaseOrderItemResponse toItemResponse(PurchaseOrderItemEntity item) {
        return PurchaseOrderItemResponse.builder()
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