package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.ENUMS.QuoteStage;
import com.resolion.crm.ENUMS.QuoteTaxType;
import com.resolion.crm.dpo.*;
import com.resolion.crm.entity.QuoteEntity;
import com.resolion.crm.entity.QuoteItemEntity;
import com.resolion.crm.respository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuoteService {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public QuoteResponse createQuote(String email, Long companyId, QuoteRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        QuoteEntity quote = QuoteEntity.builder()
                .quoteOwner(request.getQuoteOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .dealName(request.getDealName())
                .subject(request.getSubject())
                .validUntil(request.getValidUntil())
                .quoteStage(request.getQuoteStage())
                .contactName(request.getContactName())
                .team(request.getTeam())
                .accountName(request.getAccountName())
                .carrier(request.getCarrier())
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

        applyItems(quote, request.getItems());

        quote.calculateTotals();

        QuoteEntity saved = quoteRepository.save(quote);
        return toResponse(saved);
    }

    public List<QuoteResponse> getVisibleQuotes(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<QuoteEntity> quotes;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            quotes = quoteRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            quotes = quoteRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            quotes = quoteRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return quotes.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public QuoteResponse getQuoteById(String email, Long id) {
        QuoteEntity quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateAccess(email, quote.getCompanyId());

        return toResponse(quote);
    }

    public QuoteResponse updateQuote(String email, Long id, QuoteRequest request) {
        QuoteEntity quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateAccess(email, quote.getCompanyId());
        validateRequest(request);

        quote.setQuoteOwner(request.getQuoteOwner());
        quote.setOwnerEmail(request.getOwnerEmail());
        quote.setDealName(request.getDealName());
        quote.setSubject(request.getSubject());
        quote.setValidUntil(request.getValidUntil());
        quote.setQuoteStage(request.getQuoteStage());
        quote.setContactName(request.getContactName());
        quote.setTeam(request.getTeam());
        quote.setAccountName(request.getAccountName());
        quote.setCarrier(request.getCarrier());
        quote.setCopyAddress(request.getCopyAddress());
        quote.setBillingStreet(request.getBillingStreet());
        quote.setShippingStreet(request.getShippingStreet());
        quote.setBillingCity(request.getBillingCity());
        quote.setShippingCity(request.getShippingCity());
        quote.setBillingState(request.getBillingState());
        quote.setShippingState(request.getShippingState());
        quote.setBillingCode(request.getBillingCode());
        quote.setShippingCode(request.getShippingCode());
        quote.setBillingCountry(request.getBillingCountry());
        quote.setShippingCountry(request.getShippingCountry());
        quote.setDiscountAmount(request.getDiscountAmount());
        quote.setTaxType(request.getTaxType());
        quote.setTaxRate(request.getTaxRate());
        quote.setAdjustment(request.getAdjustment());
        quote.setTermsAndConditions(request.getTermsAndConditions());
        quote.setDescription(request.getDescription());

        quote.getItems().clear();
        applyItems(quote, request.getItems());

        quote.calculateTotals();

        QuoteEntity saved = quoteRepository.save(quote);
        return toResponse(saved);
    }

    public QuoteResponse updateStage(String email, Long id, QuoteStage stage) {
        QuoteEntity quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateAccess(email, quote.getCompanyId());

        quote.setQuoteStage(stage);

        QuoteEntity saved = quoteRepository.save(quote);
        return toResponse(saved);
    }

    public void deleteQuote(String email, Long id) {
        QuoteEntity quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateAccess(email, quote.getCompanyId());

        quoteRepository.delete(quote);
    }

    public List<QuoteResponse> getByStage(String email, Long companyId, QuoteStage stage) {
        validateAccess(email, companyId);

        return quoteRepository.findByCompanyIdAndQuoteStageOrderByIdDesc(companyId, stage)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void applyItems(QuoteEntity quote, List<QuoteItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("At least one quoted item is required");
        }

        int index = 1;

        for (QuoteItemRequest itemRequest : items) {
            validateItem(itemRequest);

            QuoteItemEntity item = QuoteItemEntity.builder()
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
                    .quote(quote)
                    .build();

            item.calculateLineTotals();
            quote.getItems().add(item);
            index++;
        }
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(QuoteRequest request) {
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new RuntimeException("Quote subject is required");
        }

        if (request.getDiscountAmount() != null && request.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Discount cannot be negative");
        }

        if (request.getAdjustment() != null) {
            // adjustment can be positive or negative
        }

        if (request.getTaxType() != null && request.getTaxType() != QuoteTaxType.NONE) {
            if (request.getTaxRate() == null) {
                throw new RuntimeException("Tax rate is required");
            }

            if (request.getTaxRate().compareTo(BigDecimal.ZERO) < 0 ||
                    request.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new RuntimeException("Tax rate must be between 0 and 100");
            }
        }
    }

    private void validateItem(QuoteItemRequest item) {
        if (item.getProductName() == null || item.getProductName().isBlank()) {
            throw new RuntimeException("Product name is required in quoted item");
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

    private QuoteResponse toResponse(QuoteEntity quote) {
        List<QuoteItemResponse> itemResponses = quote.getItems() == null
                ? List.of()
                : quote.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return QuoteResponse.builder()
                .id(quote.getId())
                .quoteOwner(quote.getQuoteOwner())
                .ownerEmail(quote.getOwnerEmail())
                .dealName(quote.getDealName())
                .subject(quote.getSubject())
                .validUntil(quote.getValidUntil())
                .quoteStage(quote.getQuoteStage())
                .contactName(quote.getContactName())
                .team(quote.getTeam())
                .accountName(quote.getAccountName())
                .carrier(quote.getCarrier())
                .copyAddress(quote.getCopyAddress())
                .billingStreet(quote.getBillingStreet())
                .shippingStreet(quote.getShippingStreet())
                .billingCity(quote.getBillingCity())
                .shippingCity(quote.getShippingCity())
                .billingState(quote.getBillingState())
                .shippingState(quote.getShippingState())
                .billingCode(quote.getBillingCode())
                .shippingCode(quote.getShippingCode())
                .billingCountry(quote.getBillingCountry())
                .shippingCountry(quote.getShippingCountry())
                .subTotal(quote.getSubTotal())
                .discountAmount(quote.getDiscountAmount())
                .taxType(quote.getTaxType())
                .taxRate(quote.getTaxRate())
                .taxAmount(quote.getTaxAmount())
                .adjustment(quote.getAdjustment())
                .grandTotal(quote.getGrandTotal())
                .termsAndConditions(quote.getTermsAndConditions())
                .description(quote.getDescription())
                .items(itemResponses)
                .companyId(quote.getCompanyId())
                .createdBy(quote.getCreatedBy())
                .createdDate(quote.getCreatedDate())
                .updatedDate(quote.getUpdatedDate())
                .build();
    }

    private QuoteItemResponse toItemResponse(QuoteItemEntity item) {
        return QuoteItemResponse.builder()
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