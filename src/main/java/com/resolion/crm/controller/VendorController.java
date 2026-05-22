package com.resolion.crm.controller;

import com.resolion.crm.enums.VendorCategory;
import com.resolion.crm.enums.VendorGlAccount;
import com.resolion.crm.dto.VendorRequest;
import com.resolion.crm.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping
    public ResponseEntity<?> createVendor(@RequestParam String email,
                                          @RequestParam Long companyId,
                                          @RequestBody VendorRequest request) {
        try {
            return ResponseEntity.ok(vendorService.createVendor(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleVendors(@RequestParam String email,
                                               @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(vendorService.getVisibleVendors(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            return ResponseEntity.ok(vendorService.getVendorById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVendor(@RequestParam String email,
                                          @PathVariable Long id,
                                          @RequestBody VendorRequest request) {
        try {
            return ResponseEntity.ok(vendorService.updateVendor(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVendor(@RequestParam String email,
                                          @PathVariable Long id) {
        try {
            vendorService.deleteVendor(email, id);
            return ResponseEntity.ok("Vendor deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchVendors(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(vendorService.searchVendors(email, companyId, keyword));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/gl-account")
    public ResponseEntity<?> getByGlAccount(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @RequestParam VendorGlAccount glAccount) {
        try {
            return ResponseEntity.ok(vendorService.getByGlAccount(email, companyId, glAccount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> getByCategory(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestParam VendorCategory category) {
        try {
            return ResponseEntity.ok(vendorService.getByCategory(email, companyId, category));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countVendors(@RequestParam String email,
                                          @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(vendorService.countVendors(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}