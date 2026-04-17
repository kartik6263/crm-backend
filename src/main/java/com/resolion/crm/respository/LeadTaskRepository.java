package com.resolion.crm.respository;

import com.resolion.crm.entity.LeadTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadTaskRepository extends JpaRepository<LeadTask, Long> {
    List<LeadTask>findByLeadId(Long leadId);
    List<LeadTask>findByAssignedTo(String assignedTo);
}
