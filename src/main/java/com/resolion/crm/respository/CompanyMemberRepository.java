package com.resolion.crm.respository;
import com.resolion.crm.entity.CompanyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyMemberRepository extends JpaRepository<CompanyMember,Long> {
        List<CompanyMember> findByUserIdAndActiveTrue(Long userId);
        List<CompanyMember> findByCompanyIdAndActiveTrue(Long companyId);
        Optional<CompanyMember> findByCompanyIdAndUserIdAndActiveTrue(Long companyId, Long userId);
    }

