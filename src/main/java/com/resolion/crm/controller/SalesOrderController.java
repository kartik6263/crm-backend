package com.resolion.crm.controller;

import com.resolion.crm.enums.SalesOrderStatus;
import com.resolion.crm.dto.SalesOrderRequest;
import com.resolion.crm.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<?> createSalesOrder(@RequestParam String email,
                                              @RequestParam Long companyId,
                                              @RequestBody SalesOrderRequest request) {
        try {
            return ResponseEntity.ok(salesOrderService.createSalesOrder(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleSalesOrders(@RequestParam String email,
                                                   @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(salesOrderService.getVisibleSalesOrders(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSalesOrderById(@RequestParam String email,
                                               @PathVariable Long id) {
        try {
            return ResponseEntity.ok(salesOrderService.getSalesOrderById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSalesOrder(@RequestParam String email,
                                              @PathVariable Long id,
                                              @RequestBody SalesOrderRequest request) {
        try {
            return ResponseEntity.ok(salesOrderService.updateSalesOrder(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam SalesOrderStatus status) {
        try {
            return ResponseEntity.ok(salesOrderService.updateStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSalesOrder(@RequestParam String email,
                                              @PathVariable Long id) {
        try {
            salesOrderService.deleteSalesOrder(email, id);
            return ResponseEntity.ok("Sales order deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam SalesOrderStatus status) {
        try {
            return ResponseEntity.ok(salesOrderService.getByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}