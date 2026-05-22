package com.resolion.crm.controller;

import com.resolion.crm.ENUMS.QuoteStage;
import com.resolion.crm.dpo.QuoteRequest;
import com.resolion.crm.services.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @PostMapping
    public ResponseEntity<?> createQuote(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestBody QuoteRequest request) {
        try {
            return ResponseEntity.ok(quoteService.createQuote(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleQuotes(@RequestParam String email,
                                              @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(quoteService.getVisibleQuotes(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuoteById(@RequestParam String email,
                                          @PathVariable Long id) {
        try {
            return ResponseEntity.ok(quoteService.getQuoteById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuote(@RequestParam String email,
                                         @PathVariable Long id,
                                         @RequestBody QuoteRequest request) {
        try {
            return ResponseEntity.ok(quoteService.updateQuote(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/stage")
    public ResponseEntity<?> updateStage(@RequestParam String email,
                                         @PathVariable Long id,
                                         @RequestParam QuoteStage stage) {
        try {
            return ResponseEntity.ok(quoteService.updateStage(email, id, stage));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuote(@RequestParam String email,
                                         @PathVariable Long id) {
        try {
            quoteService.deleteQuote(email, id);
            return ResponseEntity.ok("Quote deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stage")
    public ResponseEntity<?> getByStage(@RequestParam String email,
                                        @RequestParam Long companyId,
                                        @RequestParam QuoteStage stage) {
        try {
            return ResponseEntity.ok(quoteService.getByStage(email, companyId, stage));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}