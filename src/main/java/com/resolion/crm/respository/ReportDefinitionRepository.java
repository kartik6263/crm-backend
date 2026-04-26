package com.resolion.crm.respository;

import com.resolion.crm.entity.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, Long> {
    List<ReportDefinition> findByCompanyIdAndDeletedFalseOrderByIdDesc(Long companyId);
    List<ReportDefinition> findByCompanyIdAndCreatedByAndDeletedFalseOrderByIdDesc(Long companyId, String createdBy);
    List<ReportDefinition> findByCompanyIdAndFolderNameAndDeletedFalseOrderByIdDesc(Long companyId, String folderName);
    List<ReportDefinition> findByCompanyIdAndDeletedTrueOrderByIdDesc(Long companyId);
}
