package com.resolion.crm.repository;

import com.resolion.crm.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByCompanyIdOrderByEventDateDesc(Long companyId);
    List<CalendarEvent> findByCompanyIdAndEventDateOrderByEventTimeAsc(Long companyId, String eventDate);
}