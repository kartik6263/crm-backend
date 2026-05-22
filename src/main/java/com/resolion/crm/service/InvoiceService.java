package com.resolion.crm.service;

import com.resolion.crm.enums.CompanyRole;
import com.resolion.crm.enums.InvoiceStatus;
import com.resolion.crm.enums.InvoiceTaxType;
import com.resolion.crm.dto.*;
import com.resolion.crm.entity.InvoiceEntity;
import com.resolion.crm.entity.InvoiceItemEntity;
import com.resolion.crm.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public InvoiceResponse createInvoice(String email, Long companyId, InvoiceRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        if (invoiceRepository.existsByCompanyIdAndInvoiceNumberIgnoreCase(companyId, request.getInvoiceNumber())) {
            throw new RuntimeException("Invoice number already exists");
        }

        InvoiceEntity invoice = InvoiceEntity.builder()
                .invoiceOwner(request.getInvoiceOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .invoiceNumber(request.getInvoiceNumber())
                .subject(request.getSubject())
                .status(request.getStatus())
                .invoiceDate(request.getInvoiceDate())
                .dueDate(request.getDueDate())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .salesOrder(request.getSalesOrder())
                .purchaseOrder(request.getPurchaseOrder())
                .exciseDuty(request.getExciseDuty())
                .salesCommission(request.getSalesCommission())
                .accountName(request.getAccountName())
                .contactName(request.getContactName())
                .dealName(request.getDealName())
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
                .amountPaid(request.getAmountPaid())
                .termsAndConditions(request.getTermsAndConditions())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .items(new ArrayList<>())
                .build();

        applyItems(invoice, request.getItems());
        invoice.calculateTotals();

        InvoiceEntity saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    public List<InvoiceResponse> getVisibleInvoices(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<InvoiceEntity> invoices;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            invoices = invoiceRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            invoices = invoiceRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            invoices = invoiceRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return invoices.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public InvoiceResponse getInvoiceById(String email, Long id) {
        InvoiceEntity invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        validateAccess(email, invoice.getCompanyId());

        return toResponse(invoice);
    }

    public InvoiceResponse updateInvoice(String email, Long id, InvoiceRequest request) {
        InvoiceEntity invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        validateAccess(email, invoice.getCompanyId());
        validateRequest(request);

        invoiceRepository.findByCompanyIdAndInvoiceNumberIgnoreCase(
                invoice.getCompanyId(),
                request.getInvoiceNumber()
        ).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Invoice number already exists");
            }
        });

        invoice.setInvoiceOwner(request.getInvoiceOwner());
        invoice.setOwnerEmail(request.getOwnerEmail());
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setSubject(request.getSubject());
        invoice.setStatus(request.getStatus());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setCustomerName(request.getCustomerName());
        invoice.setCustomerEmail(request.getCustomerEmail());
        invoice.setSalesOrder(request.getSalesOrder());
        invoice.setPurchaseOrder(request.getPurchaseOrder());
        invoice.setExciseDuty(request.getExciseDuty());
        invoice.setSalesCommission(request.getSalesCommission());
        invoice.setAccountName(request.getAccountName());
        invoice.setContactName(request.getContactName());
        invoice.setDealName(request.getDealName());
        invoice.setCopyAddress(request.getCopyAddress());
        invoice.setBillingStreet(request.getBillingStreet());
        invoice.setShippingStreet(request.getShippingStreet());
        invoice.setBillingCity(request.getBillingCity());
        invoice.setShippingCity(request.getShippingCity());
        invoice.setBillingState(request.getBillingState());
        invoice.setShippingState(request.getShippingState());
        invoice.setBillingCode(request.getBillingCode());
        invoice.setShippingCode(request.getShippingCode());
        invoice.setBillingCountry(request.getBillingCountry());
        invoice.setShippingCountry(request.getShippingCountry());
        invoice.setDiscountAmount(request.getDiscountAmount());
        invoice.setTaxType(request.getTaxType());
        invoice.setTaxRate(request.getTaxRate());
        invoice.setAdjustment(request.getAdjustment());
        invoice.setAmountPaid(request.getAmountPaid());
        invoice.setTermsAndConditions(request.getTermsAndConditions());
        invoice.setDescription(request.getDescription());

        invoice.getItems().clear();
        applyItems(invoice, request.getItems());

        invoice.calculateTotals();

        InvoiceEntity saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    public InvoiceResponse updateStatus(String email, Long id, InvoiceStatus status) {
        InvoiceEntity invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        validateAccess(email, invoice.getCompanyId());

        invoice.setStatus(status);

        InvoiceEntity saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    public InvoiceResponse recordPayment(String email, Long id, BigDecimal amountPaid) {
        InvoiceEntity invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        validateAccess(email, invoice.getCompanyId());

        if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Paid amount cannot be negative");
        }

        invoice.setAmountPaid(amountPaid);
        invoice.calculateTotals();

        InvoiceEntity saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    public void deleteInvoice(String email, Long id) {
        InvoiceEntity invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        validateAccess(email, invoice.getCompanyId());

        invoiceRepository.delete(invoice);
    }

    public List<InvoiceResponse> getByStatus(String email, Long companyId, InvoiceStatus status) {
        validateAccess(email, companyId);

        return invoiceRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void applyItems(InvoiceEntity invoice, List<InvoiceItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("At least one invoice item is required");
        }

        int index = 1;

        for (InvoiceItemRequest itemRequest : items) {
            validateItem(itemRequest);

            InvoiceItemEntity item = InvoiceItemEntity.builder()
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
                    .invoice(invoice)
                    .build();

            item.calculateLineTotals();
            invoice.getItems().add(item);
            index++;
        }
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(InvoiceRequest request) {
        if (request.getInvoiceNumber() == null || request.getInvoiceNumber().isBlank()) {
            throw new RuntimeException("Invoice number is required");
        }

        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new RuntimeException("Subject is required");
        }

        if (request.getCustomerEmail() != null &&
                !request.getCustomerEmail().isBlank() &&
                !request.getCustomerEmail().contains("@")) {
            throw new RuntimeException("Invalid customer email");
        }

        if (request.getDueDate() != null &&
                request.getInvoiceDate() != null &&
                request.getDueDate().isBefore(request.getInvoiceDate())) {
            throw new RuntimeException("Due date cannot be before invoice date");
        }

        if (request.getDiscountAmount() != null && request.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Discount cannot be negative");
        }

        if (request.getTaxType() != null && request.getTaxType() != InvoiceTaxType.NONE) {
            if (request.getTaxRate() == null) {
                throw new RuntimeException("Tax rate is required");
            }

            if (request.getTaxRate().compareTo(BigDecimal.ZERO) < 0 ||
                    request.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new RuntimeException("Tax rate must be between 0 and 100");
            }
        }
    }

    private void validateItem(InvoiceItemRequest item) {
        if (item.getProductName() == null || item.getProductName().isBlank()) {
            throw new RuntimeException("Product name is required in invoice item");
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

    private InvoiceResponse toResponse(InvoiceEntity invoice) {
        List<InvoiceItemResponse> itemResponses = invoice.getItems() == null
                ? List.of()
                : invoice.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceOwner(invoice.getInvoiceOwner())
                .ownerEmail(invoice.getOwnerEmail())
                .invoiceNumber(invoice.getInvoiceNumber())
                .subject(invoice.getSubject())
                .status(invoice.getStatus())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .customerName(invoice.getCustomerName())
                .customerEmail(invoice.getCustomerEmail())
                .salesOrder(invoice.getSalesOrder())
                .purchaseOrder(invoice.getPurchaseOrder())
                .exciseDuty(invoice.getExciseDuty())
                .salesCommission(invoice.getSalesCommission())
                .accountName(invoice.getAccountName())
                .contactName(invoice.getContactName())
                .dealName(invoice.getDealName())
                .copyAddress(invoice.getCopyAddress())
                .billingStreet(invoice.getBillingStreet())
                .shippingStreet(invoice.getShippingStreet())
                .billingCity(invoice.getBillingCity())
                .shippingCity(invoice.getShippingCity())
                .billingState(invoice.getBillingState())
                .shippingState(invoice.getShippingState())
                .billingCode(invoice.getBillingCode())
                .shippingCode(invoice.getShippingCode())
                .billingCountry(invoice.getBillingCountry())
                .shippingCountry(invoice.getShippingCountry())
                .subTotal(invoice.getSubTotal())
                .discountAmount(invoice.getDiscountAmount())
                .taxType(invoice.getTaxType())
                .taxRate(invoice.getTaxRate())
                .taxAmount(invoice.getTaxAmount())
                .adjustment(invoice.getAdjustment())
                .grandTotal(invoice.getGrandTotal())
                .amountPaid(invoice.getAmountPaid())
                .balanceDue(invoice.getBalanceDue())
                .termsAndConditions(invoice.getTermsAndConditions())
                .description(invoice.getDescription())
                .items(itemResponses)
                .companyId(invoice.getCompanyId())
                .createdBy(invoice.getCreatedBy())
                .createdDate(invoice.getCreatedDate())
                .updatedDate(invoice.getUpdatedDate())
                .build();
    }

    private InvoiceItemResponse toItemResponse(InvoiceItemEntity item) {
        return InvoiceItemResponse.builder()
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