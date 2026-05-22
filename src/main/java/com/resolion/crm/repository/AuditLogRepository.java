package com.resolion.crm.repository;

import com.resolion.crm.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog>findTop30ByCompanyIdOrderByIdDesc(Long companyId);
    List<AuditLog> findByCompanyIdOrderByIdDesc(Long companyId);

}