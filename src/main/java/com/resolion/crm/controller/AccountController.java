package com.resolion.crm.controller;

import com.resolion.crm.ENUMS.AccountIndustry;
import com.resolion.crm.ENUMS.AccountRating;
import com.resolion.crm.ENUMS.AccountType;
import com.resolion.crm.dpo.AccountRequest;
import com.resolion.crm.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestBody AccountRequest request) {
        try {
            return ResponseEntity.ok(accountService.createAccount(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleAccounts(@RequestParam String email,
                                                @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(accountService.getVisibleAccounts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(accountService.getAccountById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@RequestParam String email,
                                           @PathVariable Long id,
                                           @RequestBody AccountRequest request) {
        try {
            return ResponseEntity.ok(accountService.updateAccount(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            accountService.deleteAccount(email, id);
            return ResponseEntity.ok("Account deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAccounts(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(accountService.searchAccounts(email, companyId, keyword));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/type")
    public ResponseEntity<?> getByAccountType(@RequestParam String email,
                                              @RequestParam Long companyId,
                                              @RequestParam AccountType accountType) {
        try {
            return ResponseEntity.ok(accountService.getByAccountType(email, companyId, accountType));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/industry")
    public ResponseEntity<?> getByIndustry(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestParam AccountIndustry industry) {
        try {
            return ResponseEntity.ok(accountService.getByIndustry(email, companyId, industry));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/rating")
    public ResponseEntity<?> getByRating(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam AccountRating rating) {
        try {
            return ResponseEntity.ok(accountService.getByRating(email, companyId, rating));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countAccounts(@RequestParam String email,
                                           @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(accountService.countAccounts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}