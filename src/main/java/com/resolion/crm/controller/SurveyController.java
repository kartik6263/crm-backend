package com.resolion.crm.controller;

import com.resolion.crm.enums.SurveyCampaignType;
import com.resolion.crm.enums.SurveyStatus;
import com.resolion.crm.dto.SurveyRequest;
import com.resolion.crm.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping
    public ResponseEntity<?> createSurvey(@RequestParam String email,
                                          @RequestParam Long companyId,
                                          @RequestBody SurveyRequest request) {
        try {
            return ResponseEntity.ok(surveyService.createSurvey(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleSurveys(@RequestParam String email,
                                               @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(surveyService.getVisibleSurveys(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSurveyById(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            return ResponseEntity.ok(surveyService.getSurveyById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSurvey(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestBody SurveyRequest request) {
        try {
            return ResponseEntity.ok(surveyService.updateSurvey(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam SurveyStatus status) {
        try {
            return ResponseEntity.ok(surveyService.updateStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSurvey(@RequestParam String email,
                                          @PathVariable Long id) {
        try {
            surveyService.deleteSurvey(email, id);
            return ResponseEntity.ok("Survey deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam SurveyStatus status) {
        try {
            return ResponseEntity.ok(surveyService.getByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/type")
    public ResponseEntity<?> getByType(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @RequestParam SurveyCampaignType type) {
        try {
            return ResponseEntity.ok(surveyService.getByType(email, companyId, type));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/between")
    public ResponseEntity<?> getSurveysBetween(@RequestParam String email,
                                               @RequestParam Long companyId,
                                               @RequestParam LocalDate startDate,
                                               @RequestParam LocalDate endDate) {
        try {
            return ResponseEntity.ok(surveyService.getSurveysBetween(email, companyId, startDate, endDate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countSurveys(@RequestParam String email,
                                          @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(surveyService.countSurveys(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}