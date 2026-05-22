package com.resolion.crm.controller;

import com.resolion.crm.ENUMS.OfflineCampaignStatus;
import com.resolion.crm.ENUMS.OfflineCampaignType;
import com.resolion.crm.dpo.OfflineCampaignRequest;
import com.resolion.crm.services.OfflineCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/offline-campaigns")
public class OfflineCampaignController {

    @Autowired
    private OfflineCampaignService offlineCampaignService;

    @PostMapping
    public ResponseEntity<?> createOfflineCampaign(@RequestParam String email,
                                                   @RequestParam Long companyId,
                                                   @RequestBody OfflineCampaignRequest request) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.createOfflineCampaign(email, companyId, request)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleOfflineCampaigns(@RequestParam String email,
                                                        @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.getVisibleOfflineCampaigns(email, companyId)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOfflineCampaignById(@RequestParam String email,
                                                    @PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.getOfflineCampaignById(email, id)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOfflineCampaign(@RequestParam String email,
                                                   @PathVariable Long id,
                                                   @RequestBody OfflineCampaignRequest request) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.updateOfflineCampaign(email, id, request)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam OfflineCampaignStatus status) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.updateStatus(email, id, status)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOfflineCampaign(@RequestParam String email,
                                                   @PathVariable Long id) {
        try {
            offlineCampaignService.deleteOfflineCampaign(email, id);
            return ResponseEntity.ok("Offline campaign deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam OfflineCampaignStatus status) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.getByStatus(email, companyId, status)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/type")
    public ResponseEntity<?> getByType(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @RequestParam OfflineCampaignType type) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.getByType(email, companyId, type)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/between")
    public ResponseEntity<?> getBetweenDates(@RequestParam String email,
                                             @RequestParam Long companyId,
                                             @RequestParam LocalDate startDate,
                                             @RequestParam LocalDate endDate) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.getBetweenDates(email, companyId, startDate, endDate)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countOfflineCampaigns(@RequestParam String email,
                                                   @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(
                    offlineCampaignService.countOfflineCampaigns(email, companyId)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}