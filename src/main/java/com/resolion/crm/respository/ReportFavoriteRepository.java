package com.resolion.crm.respository;

import com.resolion.crm.entity.ReportFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportFavoriteRepository extends JpaRepository<ReportFavorite, Long> {
    List<ReportFavorite> findByCompanyIdAndUserEmail(Long companyId, String userEmail);
    Optional<ReportFavorite> findByCompanyIdAndUserEmailAndReportId(Long companyId, String userEmail, Long reportId);
}