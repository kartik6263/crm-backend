package com.resolion.crm.controller;

import com.resolion.crm.ENUMS.ProductCategory;
import com.resolion.crm.ENUMS.ProductManufacturer;
import com.resolion.crm.dpo.ProductRequest;
import com.resolion.crm.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestBody ProductRequest request) {
        try {
            return ResponseEntity.ok(productService.createProduct(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleProducts(@RequestParam String email,
                                                @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(productService.getVisibleProducts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@RequestParam String email,
                                           @PathVariable Long id,
                                           @RequestBody ProductRequest request) {
        try {
            return ResponseEntity.ok(productService.updateProduct(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<?> updateActiveStatus(@RequestParam String email,
                                                @PathVariable Long id,
                                                @RequestParam Boolean active) {
        try {
            return ResponseEntity.ok(productService.updateActiveStatus(email, id, active));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            productService.deleteProduct(email, id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveProducts(@RequestParam String email,
                                               @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(productService.getActiveProducts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> getByCategory(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestParam ProductCategory category) {
        try {
            return ResponseEntity.ok(productService.getByCategory(email, companyId, category));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/manufacturer")
    public ResponseEntity<?> getByManufacturer(@RequestParam String email,
                                               @RequestParam Long companyId,
                                               @RequestParam ProductManufacturer manufacturer) {
        try {
            return ResponseEntity.ok(productService.getByManufacturer(email, companyId, manufacturer));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(productService.searchProducts(email, companyId, keyword));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countProducts(@RequestParam String email,
                                           @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(productService.countProducts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count/active")
    public ResponseEntity<?> countActiveProducts(@RequestParam String email,
                                                 @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(productService.countActiveProducts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}