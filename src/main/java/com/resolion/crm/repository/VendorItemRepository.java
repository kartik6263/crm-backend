package com.resolion.crm.repository;

import com.resolion.crm.entity.VendorItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorItemRepository extends JpaRepository<VendorItemEntity, Long> {

    List<VendorItemEntity> findByVendorIdOrderBySerialNoAsc(Long vendorId);
}