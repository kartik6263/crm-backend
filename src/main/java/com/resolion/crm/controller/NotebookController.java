package com.resolion.crm.controller;

import com.resolion.crm.entity.Notebook;
import com.resolion.crm.respository.NotebookRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notebook")
public class NotebookController {

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }
    }

    @GetMapping
    public List<Notebook> getNotes(@RequestParam String email,
                                   @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return notebookRepository.findByCompanyIdAndUserEmailOrderByIdDesc(companyId, email);
    }

    @PostMapping
    public Notebook createNote(@RequestParam String email,
                               @RequestParam Long companyId,
                               @RequestBody Notebook note) {
        checkAccess(email, companyId);

        note.setCompanyId(companyId);
        note.setUserEmail(email);
        note.setCreatedDate(LocalDateTime.now().toString());
        note.setUpdatedDate(LocalDateTime.now().toString());

        return notebookRepository.save(note);
    }

    @PutMapping("/{id}")
    public Notebook updateNote(@RequestParam String email,
                               @RequestParam Long companyId,
                               @PathVariable Long id,
                               @RequestBody Notebook newNote) {
        checkAccess(email, companyId);

        Notebook old = notebookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notebook not found"));

        if (!old.getCompanyId().equals(companyId) || !old.getUserEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access Denied");
        }

        old.setTitle(newNote.getTitle());
        old.setContent(newNote.getContent());
        old.setUpdatedDate(LocalDateTime.now().toString());

        return notebookRepository.save(old);
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@RequestParam String email,
                             @RequestParam Long companyId,
                             @PathVariable Long id) {
        checkAccess(email, companyId);

        Notebook note = notebookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notebook not found"));

        if (!note.getCompanyId().equals(companyId) || !note.getUserEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access Denied");
        }

        notebookRepository.delete(note);
        return "Notebook deleted";
    }
}