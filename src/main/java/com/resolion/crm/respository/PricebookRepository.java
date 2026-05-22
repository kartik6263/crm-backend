package com.resolion.crm.respository;

import com.resolion.crm.ENUMS.PricebookPricingModel;
import com.resolion.crm.entity.PricebookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PricebookRepository extends JpaRepository<PricebookEntity, Long> {

    List<PricebookEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<PricebookEntity> findByCompanyIdAndActiveOrderByIdDesc(Long companyId, Boolean active);

    List<PricebookEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<PricebookEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<PricebookEntity> findByCompanyIdAndPricingModelOrderByIdDesc(
            Long companyId,
            PricebookPricingModel pricingModel
    );

    Optional<PricebookEntity> findByCompanyIdAndPricebookNameIgnoreCase(Long companyId, String pricebookName);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndActive(Long companyId, Boolean active);

    boolean existsByCompanyIdAndPricebookNameIgnoreCase(Long companyId, String pricebookName);
}