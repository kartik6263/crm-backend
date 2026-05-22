package com.resolion.crm.controller;


import com.resolion.crm.dpo.CaseRequestDPO;
import com.resolion.crm.entity.CaseEntity;
import com.resolion.crm.services.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CaseController {

    private final CaseService caseService;

    // ======================================================
    // CREATE
    // ======================================================

    @PostMapping
    public ResponseEntity<CaseEntity> createCase(
            @RequestParam String email,
            @RequestParam Long companyId,
            @Valid @RequestBody CaseRequestDPO dpo
    ) {

        return ResponseEntity.ok(
                caseService.createCase(
                        dpo,
                        email,
                        companyId
                )
        );
    }

    // ======================================================
    // GET ALL
    // ======================================================

    @GetMapping
    public ResponseEntity<List<CaseEntity>> getAllCases(
            @RequestParam Long companyId
    ) {

        return ResponseEntity.ok(
                caseService.getAllCases(companyId)
        );
    }

    // ======================================================
    // GET ONE
    // ======================================================

    @GetMapping("/{id}")
    public ResponseEntity<CaseEntity> getCase(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                caseService.getCase(id)
        );
    }

    // ======================================================
    // DASHBOARD
    // ======================================================

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(
            @RequestParam Long companyId
    ) {

        return ResponseEntity.ok(
                caseService.dashboard(companyId)
        );
    }

    // ======================================================
    // DELETE
    // ======================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCase(
            @PathVariable Long id
    ) {

        caseService.deleteCase(id);

        return ResponseEntity.ok(
                "Case deleted successfully"
        );
    }
}