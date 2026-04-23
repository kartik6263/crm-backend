package com.resolion.crm.respository;

import com.resolion.crm.entity.StickyNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickyNoteRepository extends JpaRepository<StickyNote, Long> {
    List<StickyNote> findByCompanyIdAndUserEmailOrderByIdDesc(Long companyId, String userEmail);
}