package com.resolion.crm.repository;

import com.resolion.crm.entity.ActivityReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityReminderRepository extends JpaRepository<ActivityReminder, Long> {
    List<ActivityReminder> findByCompanyIdAndUserEmailOrderByIdDesc(Long companyId, String userEmail);
    List<ActivityReminder> findByCompanyIdAndUserEmailAndCompletedFalseOrderByIdDesc(Long companyId, String userEmail);
}
