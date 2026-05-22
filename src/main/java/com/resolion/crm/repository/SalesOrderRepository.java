package com.resolion.crm.repository;

import com.resolion.crm.enums.SalesOrderStatus;
import com.resolion.crm.entity.SalesOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrderEntity, Long> {

    List<SalesOrderEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<SalesOrderEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<SalesOrderEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<SalesOrderEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, SalesOrderStatus status);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, String SalesOrderStatus );
}