package com.leadmatrix.crm.respository;

import com.leadmatrix.crm.entity.LeadActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<LeadActivity,Long> {

    List<LeadActivity> findByLeadId(Long leadId);

}