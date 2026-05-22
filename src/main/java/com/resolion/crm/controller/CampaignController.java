package com.resolion.crm.controller;

import com.resolion.crm.enums.CampaignStatus;
import com.resolion.crm.enums.CampaignType;
import com.resolion.crm.dto.CampaignRequest;
import com.resolion.crm.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @PostMapping
    public ResponseEntity<?> createCampaign(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @RequestBody CampaignRequest request) {
        try {
            return ResponseEntity.ok(campaignService.createCampaign(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleCampaigns(@RequestParam String email,
                                                 @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(campaignService.getVisibleCampaigns(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCampaignById(@RequestParam String email,
                                             @PathVariable Long id) {
        try {
            return ResponseEntity.ok(campaignService.getCampaignById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(@RequestParam String email,
                                            @PathVariable Long id,
                                            @RequestBody CampaignRequest request) {
        try {
            return ResponseEntity.ok(campaignService.updateCampaign(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam CampaignStatus status) {
        try {
            return ResponseEntity.ok(campaignService.updateStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCampaign(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            campaignService.deleteCampaign(email, id);
            return ResponseEntity.ok("Campaign deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam CampaignStatus status) {
        try {
            return ResponseEntity.ok(campaignService.getByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/type")
    public ResponseEntity<?> getByType(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @RequestParam CampaignType type) {
        try {
            return ResponseEntity.ok(campaignService.getByType(email, companyId, type));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCampaigns(@RequestParam String email,
                                             @RequestParam Long companyId,
                                             @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(campaignService.searchCampaigns(email, companyId, keyword));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countCampaigns(@RequestParam String email,
                                            @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(campaignService.countCampaigns(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}