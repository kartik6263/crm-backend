package com.resolion.crm.respository;

import com.resolion.crm.entity.SalesOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItemEntity, Long> {

    List<SalesOrderItemEntity> findBySalesOrderIdOrderBySerialNoAsc(Long salesOrderId);
}