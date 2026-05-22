package com.resolion.crm.repository;

import com.resolion.crm.entity.LeadNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadNoteRepository
        extends JpaRepository<LeadNote,Long> {

    List<LeadNote> findByLeadId(Long leadId);

}
