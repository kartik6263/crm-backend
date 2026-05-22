package com.resolion.crm.controller;

import com.resolion.crm.entity.InvoiceEntity;
import com.resolion.crm.entity.PurchaseOrderEntity;
import com.resolion.crm.entity.QuoteEntity;
import com.resolion.crm.entity.SalesOrderEntity;
import com.resolion.crm.repository.InvoiceRepository;
import com.resolion.crm.repository.PurchaseOrderRepository;
import com.resolion.crm.repository.QuoteRepository;
import com.resolion.crm.repository.SalesOrderRepository;
import com.resolion.crm.service.CompanyAccessService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BusinessDocumentController {

    private final CompanyAccessService companyAccessService;

    private final InvoiceRepository invoiceRepository;

    private final QuoteRepository quoteRepository;

    private final SalesOrderRepository salesOrderRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    // =====================================================
    // ACCESS CHECK
    // =====================================================

    private void checkAccess(String email, Long companyId) {

        boolean hasAccess =
                companyAccessService.hasCompanyAccess(
                        email,
                        companyId
                );

        if (!hasAccess) {
            throw new RuntimeException("Access Denied");
        }
    }

    // =====================================================
    // INVOICES
    // =====================================================

    @GetMapping("/invoices")
    public List<InvoiceEntity> getInvoices(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        checkAccess(email, companyId);

        return invoiceRepository
                .findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/invoices")
    public InvoiceEntity createInvoice(
            @RequestParam String email,
            @RequestParam Long companyId,
            @RequestBody InvoiceEntity invoice
    ) {

        checkAccess(email, companyId);

        invoice.setCompanyId(companyId);

        invoice.setCreatedBy(email);

        return invoiceRepository.save(invoice);
    }

    // =====================================================
    // QUOTES
    // =====================================================

    @GetMapping("/quotes")
    public List<QuoteEntity> getQuotes(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        checkAccess(email, companyId);

        return quoteRepository
                .findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/quotes")
    public QuoteEntity createQuote(
            @RequestParam String email,
            @RequestParam Long companyId,
            @RequestBody QuoteEntity quote
    ) {

        checkAccess(email, companyId);

        quote.setCompanyId(companyId);

        quote.setCreatedBy(email);

        return quoteRepository.save(quote);
    }

    // =====================================================
    // SALES ORDERS
    // =====================================================

    @GetMapping("/sales-orders")
    public List<SalesOrderEntity> getSalesOrders(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        checkAccess(email, companyId);

        return salesOrderRepository
                .findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/sales-orders")
    public SalesOrderEntity createSalesOrder(
            @RequestParam String email,
            @RequestParam Long companyId,
            @RequestBody SalesOrderEntity order
    ) {

        checkAccess(email, companyId);

        order.setCompanyId(companyId);

        order.setCreatedBy(email);

        return salesOrderRepository.save(order);
    }

    // =====================================================
    // PURCHASE ORDERS
    // =====================================================

    @GetMapping("/purchase-orders")
    public List<PurchaseOrderEntity> getPurchaseOrders(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        checkAccess(email, companyId);

        return purchaseOrderRepository
                .findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/purchase-orders")
    public PurchaseOrderEntity createPurchaseOrder(
            @RequestParam String email,
            @RequestParam Long companyId,
            @RequestBody PurchaseOrderEntity order
    ) {

        checkAccess(email, companyId);

        order.setCompanyId(companyId);

        order.setCreatedBy(email);

        return purchaseOrderRepository.save(order);
    }
}