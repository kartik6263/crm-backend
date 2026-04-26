package com.resolion.crm.respository;

import com.resolion.crm.entity.ReportSharing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportSharingRepository extends JpaRepository<ReportSharing, Long> {
   // List<ReportSharing> findByCompanyIdAndSharedWithEmail(Long companyId, String email);
    List<ReportSharing> findByCompanyIdAndSharedWithEmail(Long companyId, String sharedWithEmail);
    Optional<ReportSharing> findByCompanyIdAndReportIdAndSharedWithEmail(Long companyId, Long reportId, String sharedWithEmail);

}