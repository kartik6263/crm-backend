package com.resolion.crm.controller;

import com.resolion.crm.enums.InvoiceStatus;
import com.resolion.crm.dto.InvoiceRequest;
import com.resolion.crm.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestBody InvoiceRequest request) {
        try {
            return ResponseEntity.ok(invoiceService.createInvoice(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleInvoices(@RequestParam String email,
                                                @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(invoiceService.getVisibleInvoices(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(invoiceService.getInvoiceById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@RequestParam String email,
                                           @PathVariable Long id,
                                           @RequestBody InvoiceRequest request) {
        try {
            return ResponseEntity.ok(invoiceService.updateInvoice(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam InvoiceStatus status) {
        try {
            return ResponseEntity.ok(invoiceService.updateStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<?> recordPayment(@RequestParam String email,
                                           @PathVariable Long id,
                                           @RequestParam BigDecimal amountPaid) {
        try {
            return ResponseEntity.ok(invoiceService.recordPayment(email, id, amountPaid));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(email, id);
            return ResponseEntity.ok("Invoice deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam InvoiceStatus status) {
        try {
            return ResponseEntity.ok(invoiceService.getByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}