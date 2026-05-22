package com.resolion.crm.controller;

import com.resolion.crm.entity.ActivityReminder;
import com.resolion.crm.entity.Announcement;
import com.resolion.crm.entity.AuditLog;
import com.resolion.crm.entity.StickyNote;
import com.resolion.crm.repository.ActivityReminderRepository;
import com.resolion.crm.repository.AnnouncementRepository;
import com.resolion.crm.repository.AuditLogRepository;
import com.resolion.crm.repository.StickyNoteRepository;
import com.resolion.crm.service.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/home-tools")
public class HomeToolsController {

    @Autowired private StickyNoteRepository stickyNoteRepository;
    @Autowired private ActivityReminderRepository reminderRepository;
    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private CompanyAccessService companyAccessService;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    // ===================== ANNOUNCEMENTS =====================

    @GetMapping("/announcements")
    public List<Announcement> announcements(@RequestParam String email,
                                            @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return announcementRepository.findByCompanyIdOrderByIdDesc(companyId);
    }

    @PostMapping("/announcements")
    public Announcement createAnnouncement(@RequestParam String email,
                                           @RequestParam Long companyId,
                                           @RequestBody Announcement announcement) {
        checkAccess(email, companyId);

        announcement.setCompanyId(companyId);
        announcement.setCreatedBy(email);
        announcement.setCreatedDate(LocalDateTime.now().toString());

        return announcementRepository.save(announcement);
    }

    // ===================== STICKY NOTES =====================

    @GetMapping("/notes")
    public List<StickyNote> getNotes(@RequestParam String email,
                                     @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return stickyNoteRepository.findByCompanyIdAndUserEmailOrderByIdDesc(companyId, email);
    }

    @PostMapping("/notes")
    public StickyNote saveNote(@RequestParam String email,
                               @RequestParam Long companyId,
                               @RequestBody StickyNote note) {
        checkAccess(email, companyId);

        note.setCompanyId(companyId);
        note.setUserEmail(email);
        note.setUpdatedDate(LocalDateTime.now().toString());

        if (note.getCreatedDate() == null) {
            note.setCreatedDate(LocalDateTime.now().toString());
        }

        return stickyNoteRepository.save(note);
    }

    @PutMapping("/notes/{id}")
    public StickyNote updateNote(@RequestParam String email,
                                 @RequestParam Long companyId,
                                 @PathVariable Long id,
                                 @RequestBody StickyNote newNote) {
        checkAccess(email, companyId);

        StickyNote old = stickyNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sticky note not found"));

        if (!old.getCompanyId().equals(companyId) || !old.getUserEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access denied");
        }

        old.setTitle(newNote.getTitle());
        old.setContent(newNote.getContent());
        old.setMinimized(newNote.getMinimized());
        old.setUpdatedDate(LocalDateTime.now().toString());

        return stickyNoteRepository.save(old);
    }

    @DeleteMapping("/notes/{id}")
    public String deleteNote(@RequestParam String email,
                             @RequestParam Long companyId,
                             @PathVariable Long id) {
        checkAccess(email, companyId);

        StickyNote note = stickyNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sticky note not found"));

        if (!note.getCompanyId().equals(companyId) || !note.getUserEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access denied");
        }

        stickyNoteRepository.delete(note);
        return "Sticky note deleted";
    }

    // ===================== REMINDERS =====================

    @PostMapping("/reminders")
    public ActivityReminder saveReminder(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestBody ActivityReminder reminder) {
        checkAccess(email, companyId);

        reminder.setCompanyId(companyId);
        reminder.setUserEmail(email);
        reminder.setCompleted(false);

        return reminderRepository.save(reminder);
    }

    @GetMapping("/reminders")
    public List<ActivityReminder> getReminders(@RequestParam String email,
                                               @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return reminderRepository.findByCompanyIdAndUserEmailAndCompletedFalseOrderByIdDesc(companyId, email);
    }

    // ===================== RECENT ITEMS =====================

    @GetMapping("/recent-items")
    public List<AuditLog> recentItems(@RequestParam String email,
                                      @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return auditLogRepository.findTop30ByCompanyIdOrderByIdDesc(companyId);
    }
}