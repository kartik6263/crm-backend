package com.resolion.crm.respository;

import com.resolion.crm.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByCompanyIdOrderByIdDesc(Long companyId);
    long countByCompanyId(Long companyId);
    long countByCompanyIdAndStatus(Long companyId, String status);
}