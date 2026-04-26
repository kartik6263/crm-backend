package com.resolion.crm.controller;

import com.resolion.crm.entity.Invoice;
import com.resolion.crm.entity.Quote;
import com.resolion.crm.entity.SalesOrder;
import com.resolion.crm.entity.PurchaseOrder;
import com.resolion.crm.respository.InvoiceRepository;
import com.resolion.crm.respository.QuoteRepository;
import com.resolion.crm.respository.SalesOrderRepository;
import com.resolion.crm.respository.PurchaseOrderRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business")
public class BusinessDocumentController {

    @Autowired private CompanyAccessService companyAccessService;
    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private QuoteRepository quoteRepository;
    @Autowired private SalesOrderRepository salesOrderRepository;
    @Autowired private PurchaseOrderRepository purchaseOrderRepository;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }
    }

    @GetMapping("/invoices")
    public List<Invoice> invoices(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return invoiceRepository.findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/invoices")
    public Invoice createInvoice(@RequestParam String email, @RequestParam Long companyId, @RequestBody Invoice invoice) {
        checkAccess(email, companyId);
        invoice.setCompanyId(companyId);
        invoice.setCreatedBy(email);
        return invoiceRepository.save(invoice);
    }

    @GetMapping("/quotes")
    public List<Quote> quotes(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return quoteRepository.findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/quotes")
    public Quote createQuote(@RequestParam String email, @RequestParam Long companyId, @RequestBody Quote quote) {
        checkAccess(email, companyId);
        quote.setCompanyId(companyId);
        quote.setCreatedBy(email);
        return quoteRepository.save(quote);
    }

    @GetMapping("/sales-orders")
    public List<SalesOrder> salesOrders(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return salesOrderRepository.findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/sales-orders")
    public SalesOrder createSalesOrder(@RequestParam String email, @RequestParam Long companyId, @RequestBody SalesOrder order) {
        checkAccess(email, companyId);
        order.setCompanyId(companyId);
        order.setCreatedBy(email);
        return salesOrderRepository.save(order);
    }

    @GetMapping("/purchase-orders")
    public List<PurchaseOrder> purchaseOrders(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return purchaseOrderRepository.findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/purchase-orders")
    public PurchaseOrder createPurchaseOrder(@RequestParam String email, @RequestParam Long companyId, @RequestBody PurchaseOrder order) {
        checkAccess(email, companyId);
        order.setCompanyId(companyId);
        order.setCreatedBy(email);
        return purchaseOrderRepository.save(order);
    }
}