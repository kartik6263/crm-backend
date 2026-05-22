package com.resolion.crm.respository;

import com.resolion.crm.entity.CaseEntity;
import com.resolion.crm.ENUMS.CasePriority;
import com.resolion.crm.ENUMS.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {

    Optional<CaseEntity> findByCaseNumber(String caseNumber);

    List<CaseEntity> findByCompanyId(Long companyId);

    List<CaseEntity> findByAssignedTo(String assignedTo);

    List<CaseEntity> findByCompanyIdAndStatus(
            Long companyId,
            CaseStatus status
    );

    List<CaseEntity> findByCompanyIdAndPriority(
            Long companyId,
            CasePriority priority
    );

    long countByCompanyId(Long companyId);

    long countByStatus(CaseStatus status);

    long countByPriority(CasePriority priority);
}