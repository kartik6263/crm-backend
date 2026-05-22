package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.ENUMS.SalesOrderStatus;
import com.resolion.crm.ENUMS.SalesOrderTaxType;
import com.resolion.crm.dpo.*;
import com.resolion.crm.entity.SalesOrderEntity;
import com.resolion.crm.entity.SalesOrderItemEntity;
import com.resolion.crm.respository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public SalesOrderResponse createSalesOrder(String email, Long companyId, SalesOrderRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        SalesOrderEntity salesOrder = SalesOrderEntity.builder()
                .salesOrderOwner(request.getSalesOrderOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .dealName(request.getDealName())
                .subject(request.getSubject())
                .purchaseOrder(request.getPurchaseOrder())
                .customerNo(request.getCustomerNo())
                .dueDate(request.getDueDate())
                .quoteName(request.getQuoteName())
                .contactName(request.getContactName())
                .pending(request.getPending())
                .exciseDuty(request.getExciseDuty())
                .carrier(request.getCarrier())
                .status(request.getStatus())
                .salesCommission(request.getSalesCommission())
                .accountName(request.getAccountName())
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

        applyItems(salesOrder, request.getItems());
        salesOrder.calculateTotals();

        SalesOrderEntity saved = salesOrderRepository.save(salesOrder);
        return toResponse(saved);
    }

    public List<SalesOrderResponse> getVisibleSalesOrders(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<SalesOrderEntity> orders;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            orders = salesOrderRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            orders = salesOrderRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            orders = salesOrderRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return orders.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SalesOrderResponse getSalesOrderById(String email, Long id) {
        SalesOrderEntity salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found"));

        validateAccess(email, salesOrder.getCompanyId());

        return toResponse(salesOrder);
    }

    public SalesOrderResponse updateSalesOrder(String email, Long id, SalesOrderRequest request) {
        SalesOrderEntity salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found"));

        validateAccess(email, salesOrder.getCompanyId());
        validateRequest(request);

        salesOrder.setSalesOrderOwner(request.getSalesOrderOwner());
        salesOrder.setOwnerEmail(request.getOwnerEmail());
        salesOrder.setDealName(request.getDealName());
        salesOrder.setSubject(request.getSubject());
        salesOrder.setPurchaseOrder(request.getPurchaseOrder());
        salesOrder.setCustomerNo(request.getCustomerNo());
        salesOrder.setDueDate(request.getDueDate());
        salesOrder.setQuoteName(request.getQuoteName());
        salesOrder.setContactName(request.getContactName());
        salesOrder.setPending(request.getPending());
        salesOrder.setExciseDuty(request.getExciseDuty());
        salesOrder.setCarrier(request.getCarrier());
        salesOrder.setStatus(request.getStatus());
        salesOrder.setSalesCommission(request.getSalesCommission());
        salesOrder.setAccountName(request.getAccountName());
        salesOrder.setCopyAddress(request.getCopyAddress());
        salesOrder.setBillingStreet(request.getBillingStreet());
        salesOrder.setShippingStreet(request.getShippingStreet());
        salesOrder.setBillingCity(request.getBillingCity());
        salesOrder.setShippingCity(request.getShippingCity());
        salesOrder.setBillingState(request.getBillingState());
        salesOrder.setShippingState(request.getShippingState());
        salesOrder.setBillingCode(request.getBillingCode());
        salesOrder.setShippingCode(request.getShippingCode());
        salesOrder.setBillingCountry(request.getBillingCountry());
        salesOrder.setShippingCountry(request.getShippingCountry());
        salesOrder.setDiscountAmount(request.getDiscountAmount());
        salesOrder.setTaxType(request.getTaxType());
        salesOrder.setTaxRate(request.getTaxRate());
        salesOrder.setAdjustment(request.getAdjustment());
        salesOrder.setTermsAndConditions(request.getTermsAndConditions());
        salesOrder.setDescription(request.getDescription());

        salesOrder.getItems().clear();
        applyItems(salesOrder, request.getItems());

        salesOrder.calculateTotals();

        SalesOrderEntity saved = salesOrderRepository.save(salesOrder);
        return toResponse(saved);
    }

    public SalesOrderResponse updateStatus(String email, Long id, SalesOrderStatus status) {
        SalesOrderEntity salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found"));

        validateAccess(email, salesOrder.getCompanyId());

        salesOrder.setStatus(status);

        SalesOrderEntity saved = salesOrderRepository.save(salesOrder);
        return toResponse(saved);
    }

    public void deleteSalesOrder(String email, Long id) {
        SalesOrderEntity salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found"));

        validateAccess(email, salesOrder.getCompanyId());

        salesOrderRepository.delete(salesOrder);
    }

    public List<SalesOrderResponse> getByStatus(String email, Long companyId, SalesOrderStatus status) {
        validateAccess(email, companyId);

        return salesOrderRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void applyItems(SalesOrderEntity salesOrder, List<SalesOrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("At least one sales order item is required");
        }

        int index = 1;

        for (SalesOrderItemRequest itemRequest : items) {
            validateItem(itemRequest);

            SalesOrderItemEntity item = SalesOrderItemEntity.builder()
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
                    .salesOrder(salesOrder)
                    .build();

            item.calculateLineTotals();
            salesOrder.getItems().add(item);
            index++;
        }
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(SalesOrderRequest request) {
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new RuntimeException("Subject is required");
        }

        if (request.getDiscountAmount() != null && request.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Discount cannot be negative");
        }

        if (request.getTaxType() != null && request.getTaxType() != SalesOrderTaxType.NONE) {
            if (request.getTaxRate() == null) {
                throw new RuntimeException("Tax rate is required");
            }

            if (request.getTaxRate().compareTo(BigDecimal.ZERO) < 0 ||
                    request.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new RuntimeException("Tax rate must be between 0 and 100");
            }
        }
    }

    private void validateItem(SalesOrderItemRequest item) {
        if (item.getProductName() == null || item.getProductName().isBlank()) {
            throw new RuntimeException("Product name is required in sales order item");
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

    private SalesOrderResponse toResponse(SalesOrderEntity salesOrder) {
        List<SalesOrderItemResponse> itemResponses = salesOrder.getItems() == null
                ? List.of()
                : salesOrder.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return SalesOrderResponse.builder()
                .id(salesOrder.getId())
                .salesOrderOwner(salesOrder.getSalesOrderOwner())
                .ownerEmail(salesOrder.getOwnerEmail())
                .dealName(salesOrder.getDealName())
                .subject(salesOrder.getSubject())
                .purchaseOrder(salesOrder.getPurchaseOrder())
                .customerNo(salesOrder.getCustomerNo())
                .dueDate(salesOrder.getDueDate())
                .quoteName(salesOrder.getQuoteName())
                .contactName(salesOrder.getContactName())
                .pending(salesOrder.getPending())
                .exciseDuty(salesOrder.getExciseDuty())
                .carrier(salesOrder.getCarrier())
                .status(salesOrder.getStatus())
                .salesCommission(salesOrder.getSalesCommission())
                .accountName(salesOrder.getAccountName())
                .copyAddress(salesOrder.getCopyAddress())
                .billingStreet(salesOrder.getBillingStreet())
                .shippingStreet(salesOrder.getShippingStreet())
                .billingCity(salesOrder.getBillingCity())
                .shippingCity(salesOrder.getShippingCity())
                .billingState(salesOrder.getBillingState())
                .shippingState(salesOrder.getShippingState())
                .billingCode(salesOrder.getBillingCode())
                .shippingCode(salesOrder.getShippingCode())
                .billingCountry(salesOrder.getBillingCountry())
                .shippingCountry(salesOrder.getShippingCountry())
                .subTotal(salesOrder.getSubTotal())
                .discountAmount(salesOrder.getDiscountAmount())
                .taxType(salesOrder.getTaxType())
                .taxRate(salesOrder.getTaxRate())
                .taxAmount(salesOrder.getTaxAmount())
                .adjustment(salesOrder.getAdjustment())
                .grandTotal(salesOrder.getGrandTotal())
                .termsAndConditions(salesOrder.getTermsAndConditions())
                .description(salesOrder.getDescription())
                .items(itemResponses)
                .companyId(salesOrder.getCompanyId())
                .createdBy(salesOrder.getCreatedBy())
                .createdDate(salesOrder.getCreatedDate())
                .updatedDate(salesOrder.getUpdatedDate())
                .build();
    }

    private SalesOrderItemResponse toItemResponse(SalesOrderItemEntity item) {
        return SalesOrderItemResponse.builder()
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