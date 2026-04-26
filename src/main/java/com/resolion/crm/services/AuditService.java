package com.resolion.crm.services;

import com.resolion.crm.entity.AuditLog;
import com.resolion.crm.respository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(Long companyId, String email, String action, String module, String recordId, String description) {
        AuditLog log = new AuditLog();
        log.setCompanyId(companyId);
        log.setUserEmail(email);
        log.setAction(action);
        log.setModuleName(module);
        log.setRecordId(recordId);
        log.setDescription(description);
        log.setCreatedDate(LocalDateTime.now().toString());

        auditLogRepository.save(log);
    }
}