package com.resolion.crm.controller;

import com.resolion.crm.ENUMS.PricebookPricingModel;
import com.resolion.crm.dpo.PricebookRequest;
import com.resolion.crm.services.PricebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricebooks")
public class PricebookController {

    @Autowired
    private PricebookService pricebookService;

    @PostMapping
    public ResponseEntity<?> createPricebook(@RequestParam String email,
                                             @RequestParam Long companyId,
                                             @RequestBody PricebookRequest request) {
        try {
            return ResponseEntity.ok(pricebookService.createPricebook(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisiblePricebooks(@RequestParam String email,
                                                  @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(pricebookService.getVisiblePricebooks(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPricebookById(@RequestParam String email,
                                              @PathVariable Long id) {
        try {
            return ResponseEntity.ok(pricebookService.getPricebookById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePricebook(@RequestParam String email,
                                             @PathVariable Long id,
                                             @RequestBody PricebookRequest request) {
        try {
            return ResponseEntity.ok(pricebookService.updatePricebook(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<?> updateActiveStatus(@RequestParam String email,
                                                @PathVariable Long id,
                                                @RequestParam Boolean active) {
        try {
            return ResponseEntity.ok(pricebookService.updateActiveStatus(email, id, active));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePricebook(@RequestParam String email,
                                             @PathVariable Long id) {
        try {
            pricebookService.deletePricebook(email, id);
            return ResponseEntity.ok("Pricebook deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActivePricebooks(@RequestParam String email,
                                                 @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(pricebookService.getActivePricebooks(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pricing-model")
    public ResponseEntity<?> getByPricingModel(@RequestParam String email,
                                               @RequestParam Long companyId,
                                               @RequestParam PricebookPricingModel pricingModel) {
        try {
            return ResponseEntity.ok(pricebookService.getByPricingModel(email, companyId, pricingModel));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countPricebooks(@RequestParam String email,
                                             @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(pricebookService.countPricebooks(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count/active")
    public ResponseEntity<?> countActivePricebooks(@RequestParam String email,
                                                   @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(pricebookService.countActivePricebooks(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}