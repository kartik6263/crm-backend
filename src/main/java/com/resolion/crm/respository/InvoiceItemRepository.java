package com.resolion.crm.respository;

import com.resolion.crm.entity.InvoiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItemEntity, Long> {

    List<InvoiceItemEntity> findByInvoiceIdOrderBySerialNoAsc(Long invoiceId);
}