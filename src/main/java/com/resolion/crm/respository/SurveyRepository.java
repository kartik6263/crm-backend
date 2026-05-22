package com.resolion.crm.respository;

import com.resolion.crm.ENUMS.SurveyCampaignType;
import com.resolion.crm.ENUMS.SurveyStatus;
import com.resolion.crm.entity.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

    List<SurveyEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<SurveyEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<SurveyEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<SurveyEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, SurveyStatus status);

    List<SurveyEntity> findByCompanyIdAndTypeOrderByIdDesc(Long companyId, SurveyCampaignType type);

    List<SurveyEntity> findByCompanyIdAndStartDateBetweenOrderByStartDateAsc(
            Long companyId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, SurveyStatus status);
}