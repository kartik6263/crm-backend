package com.resolion.crm.repository;

import com.resolion.crm.enums.InvoiceStatus;
import com.resolion.crm.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    List<InvoiceEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<InvoiceEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<InvoiceEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<InvoiceEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, InvoiceStatus status);

    Optional<InvoiceEntity> findByCompanyIdAndInvoiceNumberIgnoreCase(Long companyId, String invoiceNumber);

    boolean existsByCompanyIdAndInvoiceNumberIgnoreCase(Long companyId, String invoiceNumber);

    long countByCompanyId(Long companyId);

    //long countByCompanyIdAndStatus(Long companyId, String InvoiceStatus );
    long countByCompanyIdAndStatus(
            Long companyId,
            InvoiceStatus status
    );
}