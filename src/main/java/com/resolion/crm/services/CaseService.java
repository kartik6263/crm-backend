package com.resolion.crm.services;


import com.resolion.crm.dto.CaseRequestDTO;
import com.resolion.crm.entity.CaseEntity;
import com.resolion.crm.ENUMS.*;
import com.resolion.crm.respository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository repository;

    // ======================================================
    // CREATE CASE
    // ======================================================

    public CaseEntity createCase(
            CaseRequestDTO dto,
            String createdBy,
            Long companyId
    ) {

        CaseEntity entity = CaseEntity.builder()

                .caseNumber(generateCaseNumber())

                .companyId(companyId)

                .createdBy(createdBy)

                .caseOwner(createdBy)

                .subject(dto.getSubject())

                .status(
                        dto.getStatus() != null
                                ? dto.getStatus()
                                : CaseStatus.NEW
                )

                .type(dto.getType())

                .priority(dto.getPriority())

                .caseOrigin(dto.getCaseOrigin())

                .caseReason(dto.getCaseReason())

                .relatedLeadId(dto.getRelatedLeadId())

                .relatedContactId(dto.getRelatedContactId())

                .relatedDealId(dto.getRelatedDealId())

                .relatedAccountId(dto.getRelatedAccountId())

                .productId(dto.getProductId())

                .linkedSolutionId(dto.getLinkedSolutionId())

                .customerName(dto.getCustomerName())

                .accountName(dto.getAccountName())

                .dealName(dto.getDealName())

                .reportedBy(dto.getReportedBy())

                .email(dto.getEmail())

                .phone(dto.getPhone())

                .description(dto.getDescription())

                .internalComments(dto.getInternalComments())

                .solution(dto.getSolution())

                .assignedTo(dto.getAssignedTo())

                .createdDate(LocalDateTime.now())

                .updatedDate(LocalDateTime.now())

                .build();

        applySLA(entity);

        applyAIAnalysis(entity);

        return repository.save(entity);
    }

    // ======================================================
    // GET ALL
    // ======================================================

    public List<CaseEntity> getAllCases(Long companyId) {
        return repository.findByCompanyId(companyId);
    }

    // ======================================================
    // GET SINGLE
    // ======================================================

    public CaseEntity getCase(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Case not found")
                );
    }

    // ======================================================
    // DELETE
    // ======================================================

    public void deleteCase(Long id) {
        repository.deleteById(id);
    }

    // ======================================================
    // DASHBOARD
    // ======================================================

    public Map<String, Object> dashboard(Long companyId) {

        Map<String, Object> map = new HashMap<>();

        map.put(
                "totalCases",
                repository.countByCompanyId(companyId)
        );

        map.put(
                "openCases",
                repository.findByCompanyIdAndStatus(
                        companyId,
                        CaseStatus.OPEN
                ).size()
        );

        map.put(
                "criticalCases",
                repository.findByCompanyIdAndPriority(
                        companyId,
                        CasePriority.CRITICAL
                ).size()
        );

        return map;
    }

    // ======================================================
    // SLA ENGINE
    // ======================================================

    private void applySLA(CaseEntity entity) {

        if (entity.getPriority() == CasePriority.CRITICAL) {

            entity.setSlaPolicy("CRITICAL");

            entity.setResponseTimeHours(1);

            entity.setResolutionTimeHours(4);

        } else if (entity.getPriority() == CasePriority.HIGH) {

            entity.setSlaPolicy("HIGH");

            entity.setResponseTimeHours(2);

            entity.setResolutionTimeHours(8);

        } else if (entity.getPriority() == CasePriority.MEDIUM) {

            entity.setSlaPolicy("MEDIUM");

            entity.setResponseTimeHours(8);

            entity.setResolutionTimeHours(24);

        } else {

            entity.setSlaPolicy("LOW");

            entity.setResponseTimeHours(24);

            entity.setResolutionTimeHours(72);
        }

        entity.setFirstResponseDue(
                LocalDateTime.now()
                        .plusHours(entity.getResponseTimeHours())
        );

        entity.setResolutionDue(
                LocalDateTime.now()
                        .plusHours(entity.getResolutionTimeHours())
        );
    }

    // ======================================================
    // AI ANALYSIS
    // ======================================================

    private void applyAIAnalysis(CaseEntity entity) {

        entity.setAiCategory(
                entity.getType() != null
                        ? entity.getType().name()
                        : "GENERAL"
        );

        entity.setAiSentiment("NEUTRAL");

        entity.setAiConfidence(85.0);

        entity.setAiSummary(
                "AI generated summary for case: "
                        + entity.getSubject()
        );

        entity.setAiSuggestedSolution(
                "Suggested troubleshooting steps generated by AI."
        );
    }

    // ======================================================
    // CASE NUMBER GENERATOR
    // ======================================================

    private String generateCaseNumber() {

        return "CASE-"
                + LocalDateTime.now().getYear()
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }



}