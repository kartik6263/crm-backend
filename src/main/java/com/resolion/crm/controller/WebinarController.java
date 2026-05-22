package com.resolion.crm.controller;

import com.resolion.crm.enums.WebinarStatus;
import com.resolion.crm.enums.WebinarType;
import com.resolion.crm.dto.WebinarRequest;
import com.resolion.crm.service.WebinarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/webinars")
public class WebinarController {

    @Autowired
    private WebinarService webinarService;

    @PostMapping
    public ResponseEntity<?> createWebinar(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestBody WebinarRequest request) {
        try {
            return ResponseEntity.ok(webinarService.createWebinar(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleWebinars(@RequestParam String email,
                                                @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(webinarService.getVisibleWebinars(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWebinarById(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(webinarService.getWebinarById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWebinar(@RequestParam String email,
                                           @PathVariable Long id,
                                           @RequestBody WebinarRequest request) {
        try {
            return ResponseEntity.ok(webinarService.updateWebinar(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam WebinarStatus status) {
        try {
            return ResponseEntity.ok(webinarService.updateStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWebinar(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            webinarService.deleteWebinar(email, id);
            return ResponseEntity.ok("Webinar deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam WebinarStatus status) {
        try {
            return ResponseEntity.ok(webinarService.getByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/type")
    public ResponseEntity<?> getByType(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @RequestParam WebinarType type) {
        try {
            return ResponseEntity.ok(webinarService.getByType(email, companyId, type));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/between")
    public ResponseEntity<?> getWebinarsBetween(@RequestParam String email,
                                                @RequestParam Long companyId,
                                                @RequestParam LocalDateTime start,
                                                @RequestParam LocalDateTime end) {
        try {
            return ResponseEntity.ok(webinarService.getWebinarsBetween(email, companyId, start, end));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countWebinars(@RequestParam String email,
                                           @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(webinarService.countWebinars(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}