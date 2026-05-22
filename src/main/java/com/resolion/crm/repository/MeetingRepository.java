package com.resolion.crm.repository;

import com.resolion.crm.enums.MeetingRelatedType;
import com.resolion.crm.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<MeetingEntity, Long> {

    List<MeetingEntity> findByCompanyIdOrderByIdDesc(Long companyId);

    List<MeetingEntity> findByCompanyIdAndHostEmailOrderByIdDesc(Long companyId, String hostEmail);

    List<MeetingEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);

    List<MeetingEntity> findByCompanyIdAndRelatedTypeAndRelatedIdOrderByIdDesc(
            Long companyId,
            MeetingRelatedType relatedType,
            Long relatedId
    );

    List<MeetingEntity> findByCompanyIdAndFromDateTimeBetweenOrderByFromDateTimeAsc(
            Long companyId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndFromDateTimeBetween(
            Long companyId,
            LocalDateTime start,
            LocalDateTime end
    );
}