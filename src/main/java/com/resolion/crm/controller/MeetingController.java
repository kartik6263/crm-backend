package com.resolion.crm.controller;

import com.resolion.crm.enums.MeetingRelatedType;
import com.resolion.crm.dto.MeetingRequest;
import com.resolion.crm.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping
    public ResponseEntity<?> createMeeting(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestBody MeetingRequest request) {
        try {
            return ResponseEntity.ok(meetingService.createMeeting(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleMeetings(@RequestParam String email,
                                                @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(meetingService.getVisibleMeetings(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeetingById(@RequestParam String email,
                                            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(meetingService.getMeetingById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeeting(@RequestParam String email,
                                           @PathVariable Long id,
                                           @RequestBody MeetingRequest request) {
        try {
            return ResponseEntity.ok(meetingService.updateMeeting(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeeting(@RequestParam String email,
                                           @PathVariable Long id) {
        try {
            meetingService.deleteMeeting(email, id);
            return ResponseEntity.ok("Meeting deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/between")
    public ResponseEntity<?> getMeetingsBetween(@RequestParam String email,
                                                @RequestParam Long companyId,
                                                @RequestParam LocalDateTime start,
                                                @RequestParam LocalDateTime end) {
        try {
            return ResponseEntity.ok(meetingService.getMeetingsBetween(email, companyId, start, end));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/related")
    public ResponseEntity<?> getMeetingsByRelated(@RequestParam String email,
                                                  @RequestParam Long companyId,
                                                  @RequestParam MeetingRelatedType relatedType,
                                                  @RequestParam Long relatedId) {
        try {
            return ResponseEntity.ok(
                    meetingService.getMeetingsByRelated(email, companyId, relatedType, relatedId)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}