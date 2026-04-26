package com.resolion.crm.respository;

import com.resolion.crm.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByCompanyIdOrderByIdDesc(Long companyId);
    long countByCompanyId(Long companyId);
    long countByCompanyIdAndStatus(Long companyId, String status);
}