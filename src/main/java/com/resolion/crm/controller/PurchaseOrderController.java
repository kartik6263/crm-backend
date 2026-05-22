package com.resolion.crm.controller;

import com.resolion.crm.enums.PurchaseOrderStatus;
import com.resolion.crm.dto.PurchaseOrderRequest;
import com.resolion.crm.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<?> createPurchaseOrder(@RequestParam String email,
                                                 @RequestParam Long companyId,
                                                 @RequestBody PurchaseOrderRequest request) {
        try {
            return ResponseEntity.ok(purchaseOrderService.createPurchaseOrder(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisiblePurchaseOrders(@RequestParam String email,
                                                      @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(purchaseOrderService.getVisiblePurchaseOrders(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseOrderById(@RequestParam String email,
                                                  @PathVariable Long id) {
        try {
            return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePurchaseOrder(@RequestParam String email,
                                                 @PathVariable Long id,
                                                 @RequestBody PurchaseOrderRequest request) {
        try {
            return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrder(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam PurchaseOrderStatus status) {
        try {
            return ResponseEntity.ok(purchaseOrderService.updateStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePurchaseOrder(@RequestParam String email,
                                                 @PathVariable Long id) {
        try {
            purchaseOrderService.deletePurchaseOrder(email, id);
            return ResponseEntity.ok("Purchase order deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam PurchaseOrderStatus status) {
        try {
            return ResponseEntity.ok(purchaseOrderService.getByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}