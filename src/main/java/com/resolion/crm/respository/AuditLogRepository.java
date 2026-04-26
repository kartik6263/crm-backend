package com.resolion.crm.respository;

import com.resolion.crm.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByCompanyIdOrderByIdDesc(Long companyId);
}