package com.resolion.crm.respository;

import com.resolion.crm.entity.CaseCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseCommentRepository
        extends JpaRepository<CaseCommentEntity, Long> {

    List<CaseCommentEntity>
    findByCaseIdOrderByCreatedDateAsc(Long caseId);
}