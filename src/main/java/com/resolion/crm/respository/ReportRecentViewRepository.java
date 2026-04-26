package com.resolion.crm.respository;

import com.resolion.crm.entity.ReportRecentView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRecentViewRepository extends JpaRepository<ReportRecentView, Long> {
    List<ReportRecentView> findTop20ByCompanyIdAndUserEmailOrderByIdDesc(Long companyId, String userEmail);
}