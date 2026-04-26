package com.resolion.crm.respository;

import com.resolion.crm.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    List<SalesOrder> findByCompanyIdOrderByIdDesc(Long companyId);
    long countByCompanyId(Long companyId);
    long countByCompanyIdAndStatus(Long companyId, String status);
}