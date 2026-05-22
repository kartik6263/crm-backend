package com.resolion.crm.repository;

import com.resolion.crm.enums.TaskStatus;
import com.resolion.crm.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity>findByLeadId(Long leadId);
    List<TaskEntity>findByAssignedTo(String assignedTo);

    List<TaskEntity> findByCompanyIdOrderByIdDesc(Long companyId);
    List<TaskEntity> findByCompanyIdAndAssignedToOrderByIdDesc(Long companyId, String assignedTo);
    List<TaskEntity> findByCompanyIdAndCreatedByOrderByIdDesc(Long companyId, String createdBy);
    List<TaskEntity> findByCompanyIdAndLeadIdOrderByIdDesc(Long companyId, Long leadId);
    List<TaskEntity> findByCompanyIdAndStatusOrderByIdDesc(Long companyId, TaskStatus status);
    List<TaskEntity> findByCompanyIdAndDueDateOrderByIdDesc(Long companyId, LocalDate dueDate);
    List<TaskEntity> findByCompanyIdAndAssignedToAndStatusOrderByIdDesc(
            Long companyId,
            String assignedTo,
            TaskStatus status
    );

    long countByCompanyId(Long companyId);
    long countByCompanyIdAndStatus(Long companyId, TaskStatus status);
    long countByCompanyIdAndDueDate(Long companyId, LocalDate dueDate);

}
