package com.resolion.crm.respository;

import com.resolion.crm.entity.CompanyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyUsageRepository extends JpaRepository<CompanyUsage, Long> {
    Optional<CompanyUsage> findByCompanyId(Long companyId);
}