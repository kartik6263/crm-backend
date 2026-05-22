package com.resolion.crm.respository;

import com.resolion.crm.ENUMS.WebinarStatus;
import com.resolion.crm.ENUMS.WebinarType;
import com.resolion.crm.entity.WebinarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WebinarRepository extends JpaRepository<WebinarEntity, Long> {

    List<WebinarEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<WebinarEntity> findByCompanyIdAndOwnerEmailOrderByIdDesc(Long companyId, String ownerEmail);

    List<WebinarEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<WebinarEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, WebinarStatus status);

    List<WebinarEntity> findByCompanyIdAndTypeOrderByIdDesc(Long companyId, WebinarType type);

    List<WebinarEntity> findByCompanyIdAndWebinarScheduleBetweenOrderByWebinarScheduleAsc(
            Long companyId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, WebinarStatus status);
}