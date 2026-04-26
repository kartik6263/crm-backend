package com.resolion.crm.respository;

import com.resolion.crm.entity.ReportFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportFilterRepository extends JpaRepository<ReportFilter, Long> {
    List<ReportFilter> findByCompanyIdAndReportIdAndUserEmailOrderByIdDesc(Long companyId, Long reportId, String userEmail);
}