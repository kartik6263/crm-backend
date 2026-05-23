package com.resolion.crm.repository;

import com.resolion.crm.entity.LeadmatrixEntity;
import com.resolion.crm.enums.LeadSource;
import com.resolion.crm.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadmatrixRespository
        extends JpaRepository<LeadmatrixEntity, Long> {

    // =========================================================
    // PAGINATION
    // =========================================================

    @Override
    Page<LeadmatrixEntity> findAll(Pageable pageable);

    // =========================================================
    // BASIC SEARCH
    // =========================================================

    List<LeadmatrixEntity> findByFirstName(String firstName);

    List<LeadmatrixEntity> findByAssignedTo(String assignedTo);

    List<LeadmatrixEntity> findByCompanyId(Long companyId);

    List<LeadmatrixEntity> findByScoreGreaterThan(Integer score);

    // =========================================================
    // COMPANY FILTERS
    // =========================================================

    List<LeadmatrixEntity> findByCompanyIdAndAssignedTo(
            Long companyId,
            String assignedTo
    );

    List<LeadmatrixEntity> findByCompanyIdAndCreatedBy(
            Long companyId,
            String createdBy
    );

    List<LeadmatrixEntity> findByCompanyIdAndAssignedToIsNotNull(
            Long companyId
    );

    List<LeadmatrixEntity> findByCompanyIdAndScoreGreaterThan(
            Long companyId,
            Integer score
    );

    // =========================================================
    // STATUS FILTERS
    // =========================================================

    List<LeadmatrixEntity> findByStatus(LeadStatus status);

    List<LeadmatrixEntity> findByCompanyIdAndStatus(
            Long companyId,
            LeadStatus status
    );

    long countByStatus(LeadStatus status);

    long countByCompanyIdAndStatus(
            Long companyId,
            LeadStatus status
    );

    long countByCompanyIdAndStatusIgnoreCase(
            Long companyId,
            String status
    );
    long countByCompanyIdAndAssignedTo(
            Long companyId,
            String assignedTo
    );

    long countByCompanyIdAndAssignedToAndStatus(
            Long companyId,
            String assignedTo,
            LeadStatus status
    );


    // =========================================================
    // SOURCE FILTERS
    // =========================================================

    List<LeadmatrixEntity> findByCompanyIdAndSource(
            Long companyId,
            LeadSource source
    );

    long countBySource(LeadSource source);

    long countByCompanyIdAndSource(
            Long companyId,
            LeadSource source
    );

    long countByCompanyIdAndSourceIgnoreCase(
            Long companyId,
            String source
    );

    // =========================================================
    // OTHER COUNTS
    // =========================================================

    long countByAssignedTo(String assignedTo);

    long countByCreatedDate(String createdDate);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndCreatedDate(
            Long companyId,
            String createdDate
    );
}