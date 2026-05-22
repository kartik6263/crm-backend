package com.resolion.crm.repository;

import com.resolion.crm.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCompanyIdOrderByIdDesc(Long companyId);
}