package com.resolion.crm.repository;

import com.resolion.crm.entity.PurchaseOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItemEntity, Long> {

    List<PurchaseOrderItemEntity> findByPurchaseOrderIdOrderBySerialNoAsc(Long purchaseOrderId);
}