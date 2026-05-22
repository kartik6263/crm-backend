package com.resolion.crm.respository;


import com.resolion.crm.ENUMS.LeadSource;
import com.resolion.crm.ENUMS.LeadStatus;
import com.resolion.crm.entity.LeadmatrixEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadmatrixRespository extends JpaRepository<LeadmatrixEntity, Long> {

    Page<LeadmatrixEntity> findAll(Pageable pageable);

    List<LeadmatrixEntity> findByName(String name);
    List<LeadmatrixEntity> findByStatus(String status);
    List<LeadmatrixEntity> findByAssignedTo(String assignedTo);
    List<LeadmatrixEntity> findByCompanyId(Long companyId);
    List<LeadmatrixEntity> findByScoreGreaterThan(Integer score);

    List<LeadmatrixEntity> findByCompanyIdAndAssignedTo(Long companyId, String assignedTo);
    List<LeadmatrixEntity> findByCompanyIdAndStatus(Long companyId, String status);
    List<LeadmatrixEntity> findByCompanyIdAndName(Long companyId, String name);
    List<LeadmatrixEntity> findByCompanyIdAndCreatedBy(Long companyId, String createdBy);

    List<LeadmatrixEntity> findByCompanyIdAndAssignedToIsNotNull(Long companyId);
    List<LeadmatrixEntity> findByCompanyIdAndScoreGreaterThan(Long companyId, Integer score);

    List<LeadmatrixEntity> findByCompanyIdAndStatus(Long companyId, LeadStatus status);
    List<LeadmatrixEntity> findByCompanyIdAndSource(Long companyId, LeadSource source);


    @Override
    long count();
    long countByStatus(String status);
    long countByAssignedTo(String assignedTo);
    long countBySource(String source);
    long countByCreatedDate(String createdDate);

    long countByCompanyId(Long companyId);
    long countByCompanyIdAndStatus(Long companyId, String status);
    long countByCompanyIdAndAssignedTo(Long companyId, String assignedTo);
    long countByCompanyIdAndSource(Long companyId, String source);
    long countByCompanyIdAndCreatedDate(Long companyId, String createdDate);

    long countByCompanyIdAndAssignedToAndStatus(Long companyId, String assignedTo, String status);

    long countByCompanyIdAndStatusIgnoreCase(Long companyId, String status);
    long countByCompanyIdAndSourceIgnoreCase(Long companyId, String source);

    long countByCompanyIdAndStatus(Long companyId, LeadStatus status);
    long countByCompanyIdAndSource(Long companyId, LeadSource source);
}