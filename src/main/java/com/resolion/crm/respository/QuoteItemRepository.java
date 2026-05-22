package com.resolion.crm.respository;

import com.resolion.crm.entity.QuoteItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteItemRepository extends JpaRepository<QuoteItemEntity, Long> {

    List<QuoteItemEntity> findByQuoteIdOrderBySerialNoAsc(Long quoteId);
}