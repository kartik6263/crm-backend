package com.resolion.crm.controller;

import com.resolion.crm.entity.Announcement;
import com.resolion.crm.entity.StickyNote;
import com.resolion.crm.respository.AnnouncementRepository;
import com.resolion.crm.respository.StickyNoteRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/widgets")
public class HomeWidgetController {

    @Autowired
    private CompanyAccessService companyAccessService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private StickyNoteRepository stickyNoteRepository;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }
    }

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

    @GetMapping("/sticky-notes")
    public List<StickyNote> stickyNotes(@RequestParam String email,
                                        @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return stickyNoteRepository.findByCompanyIdAndUserEmailOrderByIdDesc(companyId, email);
    }

    @PostMapping("/sticky-notes")
    public StickyNote createStickyNote(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @RequestBody StickyNote note) {
        checkAccess(email, companyId);

        note.setCompanyId(companyId);
        note.setUserEmail(email);
        note.setCreatedDate(LocalDateTime.now().toString());
        note.setUpdatedDate(LocalDateTime.now().toString());

        return stickyNoteRepository.save(note);
    }

    @PutMapping("/sticky-notes/{id}")
    public StickyNote updateStickyNote(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @PathVariable Long id,
                                       @RequestBody StickyNote newNote) {
        checkAccess(email, companyId);

        StickyNote old = stickyNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sticky note not found"));

        if (!old.getCompanyId().equals(companyId) || !old.getUserEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access Denied");
        }

        old.setTitle(newNote.getTitle());
        old.setContent(newNote.getContent());
        old.setMinimized(newNote.getMinimized());
        old.setUpdatedDate(LocalDateTime.now().toString());

        return stickyNoteRepository.save(old);
    }

    @DeleteMapping("/sticky-notes/{id}")
    public String deleteStickyNote(@RequestParam String email,
                                   @RequestParam Long companyId,
                                   @PathVariable Long id) {
        checkAccess(email, companyId);

        StickyNote note = stickyNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sticky note not found"));

        if (!note.getCompanyId().equals(companyId) || !note.getUserEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access Denied");
        }

        stickyNoteRepository.delete(note);
        return "Sticky note deleted";
    }
}