package com.resolion.crm.respository;

import com.resolion.crm.entity.ReportFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportFolderRepository extends JpaRepository<ReportFolder, Long> {
    List<ReportFolder> findByCompanyIdOrderByIdAsc(Long companyId);
    List<ReportFolder> findByCompanyIdAndVisibleTrueOrderByIdAsc(Long companyId);
    Optional<ReportFolder> findByCompanyIdAndName(Long companyId, String name);
}