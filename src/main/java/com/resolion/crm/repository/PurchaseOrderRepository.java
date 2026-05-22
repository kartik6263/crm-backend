package com.resolion.crm.repository;

import com.resolion.crm.enums.PurchaseOrderStatus;
import com.resolion.crm.entity.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {

    List<PurchaseOrderEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<PurchaseOrderEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<PurchaseOrderEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<PurchaseOrderEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, PurchaseOrderStatus status);

    Optional<PurchaseOrderEntity> findByCompanyIdAndPoNumberIgnoreCase(Long companyId, String poNumber);

    boolean existsByCompanyIdAndPoNumberIgnoreCase(Long companyId, String poNumber);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, String PurchaseOrderStatus );
}