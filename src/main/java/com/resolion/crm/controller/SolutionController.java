package com.resolion.crm.controller;

import com.resolion.crm.ENUMS.SolutionStatus;
import com.resolion.crm.dpo.SolutionRequest;
import com.resolion.crm.services.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solutions")
public class SolutionController {

    @Autowired
    private SolutionService solutionService;

    @PostMapping
    public ResponseEntity<?> createSolution(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @RequestBody SolutionRequest request) {
        try {
            return ResponseEntity.ok(solutionService.createSolution(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleSolutions(@RequestParam String email,
                                                 @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(solutionService.getVisibleSolutions(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSolutionById(@RequestParam String email,
                                             @PathVariable Long id) {
        try {
            return ResponseEntity.ok(solutionService.getSolutionById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSolution(@RequestParam String email,
                                            @PathVariable Long id,
                                            @RequestBody SolutionRequest request) {
        try {
            return ResponseEntity.ok(solutionService.updateSolution(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestParam SolutionStatus status) {
        try {
            return ResponseEntity.ok(solutionService.updateStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSolution(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            solutionService.deleteSolution(email, id);
            return ResponseEntity.ok("Solution deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam SolutionStatus status) {
        try {
            return ResponseEntity.ok(solutionService.getByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/published")
    public ResponseEntity<?> getPublishedSolutions(@RequestParam String email,
                                                   @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(solutionService.getPublishedSolutions(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSolutions(@RequestParam String email,
                                             @RequestParam Long companyId,
                                             @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(solutionService.searchSolutions(email, companyId, keyword));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countSolutions(@RequestParam String email,
                                            @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(solutionService.countSolutions(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}