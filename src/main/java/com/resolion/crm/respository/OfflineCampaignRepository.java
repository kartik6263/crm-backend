package com.resolion.crm.respository;

import com.resolion.crm.ENUMS.OfflineCampaignStatus;
import com.resolion.crm.ENUMS.OfflineCampaignType;
import com.resolion.crm.entity.OfflineCampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OfflineCampaignRepository extends JpaRepository<OfflineCampaignEntity, Long> {

    List<OfflineCampaignEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<OfflineCampaignEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<OfflineCampaignEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<OfflineCampaignEntity> findByCompanyIdAndStatusOrderByIdDesc(
            Long companyId,
            OfflineCampaignStatus status
    );

    List<OfflineCampaignEntity> findByCompanyIdAndTypeOrderByIdDesc(
            Long companyId,
            OfflineCampaignType type
    );

    List<OfflineCampaignEntity> findByCompanyIdAndStartDateBetweenOrderByStartDateAsc(
            Long companyId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, OfflineCampaignStatus status);
}