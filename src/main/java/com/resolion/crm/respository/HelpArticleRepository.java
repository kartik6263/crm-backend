package com.resolion.crm.respository;

import com.resolion.crm.entity.HelpArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HelpArticleRepository extends JpaRepository<HelpArticle, Long> {
    List<HelpArticle> findByCategoryIgnoreCase(String category);
}