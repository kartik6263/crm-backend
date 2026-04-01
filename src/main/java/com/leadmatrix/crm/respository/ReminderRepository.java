package com.leadmatrix.crm.respository;

import com.leadmatrix.crm.entity.LeadReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository
        extends JpaRepository<LeadReminder,Long> {

    List<LeadReminder> findByReminderDate(String reminderDate);
    List<LeadReminder> findByLeadId(Long leadId);

}