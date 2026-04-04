package com.leadmatrix.crm.respository;


import com.leadmatrix.crm.entity.LeadmatrixEntity;
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
    List<LeadmatrixEntity> findByScoreGreaterThan(int score);
    @Override
    long count();
    long countByStatus(String status);
    long countByAssignedTo(String assignedTo);
    long countBySource(String source);
    long countByCreatedDate(String createdDate);
}