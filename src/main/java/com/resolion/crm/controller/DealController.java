package com.resolion.crm.controller;


import com.resolion.crm.enums.DealStage;
import com.resolion.crm.dto.DealRequest;
import com.resolion.crm.dto.DealResponse;
import com.resolion.crm.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
public class DealController {

    @Autowired
    private DealService dealService;

    @PostMapping
    public ResponseEntity<?> createDeal(@RequestParam String email,
                                        @RequestParam Long companyId,
                                        @RequestBody DealRequest request) {
        try {
            DealResponse response = dealService.createDeal(email, companyId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleDeals(@RequestParam String email,
                                             @RequestParam Long companyId) {
        try {
            List<DealResponse> deals = dealService.getVisibleDeals(email, companyId);
            return ResponseEntity.ok(deals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDealById(@RequestParam String email,
                                         @PathVariable Long id) {
        try {
            DealResponse response = dealService.getDealById(email, id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDeal(@RequestParam String email,
                                        @PathVariable Long id,
                                        @RequestBody DealRequest request) {
        try {
            DealResponse response = dealService.updateDeal(email, id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeal(@RequestParam String email,
                                        @PathVariable Long id) {
        try {
            dealService.deleteDeal(email, id);
            return ResponseEntity.ok("Deal deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stage")
    public ResponseEntity<?> getDealsByStage(@RequestParam String email,
                                             @RequestParam Long companyId,
                                             @RequestParam DealStage stage) {
        try {
            return ResponseEntity.ok(dealService.getDealsByStage(email, companyId, stage));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countDeals(@RequestParam String email,
                                        @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(dealService.countDeals(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count/stage")
    public ResponseEntity<?> countDealsByStage(@RequestParam String email,
                                               @RequestParam Long companyId,
                                               @RequestParam DealStage stage) {
        try {
            return ResponseEntity.ok(dealService.countDealsByStage(email, companyId, stage));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}