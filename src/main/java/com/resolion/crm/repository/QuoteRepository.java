package com.resolion.crm.repository;

import com.resolion.crm.enums.QuoteStage;
import com.resolion.crm.entity.QuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<QuoteEntity, Long> {

    List<QuoteEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<QuoteEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<QuoteEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<QuoteEntity> findByCompanyIdAndQuoteStageOrderByIdDesc(Long companyId, QuoteStage quoteStage);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndQuoteStage(Long companyId, String QuoteStage );
}