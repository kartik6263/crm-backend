package com.resolion.crm.respository;

import com.resolion.crm.entity.RecentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentItemRepository extends JpaRepository<RecentItem, Long> {
    List<RecentItem> findTop20ByCompanyIdAndUserEmailOrderByIdDesc(Long companyId, String userEmail);
}