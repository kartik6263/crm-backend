package com.resolion.crm.controller;

import com.resolion.crm.enums.ContactLeadSource;
import com.resolion.crm.dto.ContactRequest;
import com.resolion.crm.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<?> createContact(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestBody ContactRequest request) {
        try {
            return ResponseEntity.ok(contactService.createContact(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleContacts(@RequestParam String email,
                                                @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(contactService.getVisibleContacts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContactById(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(contactService.getContactById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@RequestParam String email,
                                           @PathVariable Long id,
                                           @RequestBody ContactRequest request) {
        try {
            return ResponseEntity.ok(contactService.updateContact(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            contactService.deleteContact(email, id);
            return ResponseEntity.ok("Contact deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchContacts(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(contactService.searchContacts(email, companyId, keyword));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lead-source")
    public ResponseEntity<?> getContactsByLeadSource(@RequestParam String email,
                                                     @RequestParam Long companyId,
                                                     @RequestParam ContactLeadSource leadSource) {
        try {
            return ResponseEntity.ok(contactService.getContactsByLeadSource(email, companyId, leadSource));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countContacts(@RequestParam String email,
                                           @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(contactService.countContacts(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}