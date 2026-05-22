package com.resolion.crm.services;

import com.resolion.crm.entity.CaseCommentEntity;
import com.resolion.crm.respository.CaseCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseCommentService {

    @Autowired
    private CaseCommentRepository repository;

    public CaseCommentEntity addComment(
            Long caseId,
            Long companyId,
            String email,
            String comment,
            Boolean internal
    ) {

        CaseCommentEntity entity =
                CaseCommentEntity.builder()
                        .caseId(caseId)
                        .companyId(companyId)
                        .commentedBy(email)
                        .comment(comment)
                        .internalComment(internal)
                        .createdDate(LocalDateTime.now())
                        .build();

        return repository.save(entity);
    }

    public List<CaseCommentEntity> getComments(Long caseId) {
        return repository.findByCaseIdOrderByCreatedDateAsc(caseId);
    }
}