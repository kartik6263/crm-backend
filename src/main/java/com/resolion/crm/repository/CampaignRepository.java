package com.resolion.crm.repository;

import com.resolion.crm.enums.CampaignStatus;
import com.resolion.crm.enums.CampaignType;
import com.resolion.crm.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {

    List<CampaignEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<CampaignEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<CampaignEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<CampaignEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, CampaignStatus status);

    List<CampaignEntity> findByCompanyIdAndTypeOrderByIdDesc(Long companyId, CampaignType type);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, CampaignStatus status);

    @Query("""
           SELECT c FROM CampaignEntity c
           WHERE c.companyId = :companyId
           AND (
                LOWER(c.campaignName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.campaignSubject) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.senderName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.senderAddress) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
           ORDER BY c.id DESC
           """)
    List<CampaignEntity> searchByKeyword(@Param("companyId") Long companyId,
                                         @Param("keyword") String keyword);
}