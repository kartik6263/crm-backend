package com.resolion.crm.repository;

import com.resolion.crm.enums.DealStage;
import com.resolion.crm.entity.DealEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<DealEntity, Long> {

    List<DealEntity> findByCompanyIdOrderByIdDesc(Long companyId);
    List<DealEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);
    List<DealEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);
    List<DealEntity> findByCompanyIdAndStageIgnoreCaseOrderByIdDesc(Long companyId, String stage);



    List<DealEntity> findByCompanyIdAndStageOrderByIdDesc(Long companyId, DealStage stage);

    long countByCompanyIdAndStage(Long companyId, DealStage stage);

    long countByCompanyId(Long companyId);
    long countByCompanyIdAndStageIgnoreCase(Long companyId, String stage);
}