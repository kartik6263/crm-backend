package com.resolion.crm.controller;

import com.resolion.crm.entity.HelpArticle;
import com.resolion.crm.respository.HelpArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/help")
public class HelpController {

    @Autowired
    private HelpArticleRepository helpArticleRepository;

    @GetMapping("/articles")
    public List<HelpArticle> getArticles(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return helpArticleRepository.findByCategoryIgnoreCase(category);
        }

        return helpArticleRepository.findAll();
    }

    @PostMapping("/articles")
    public HelpArticle createArticle(@RequestBody HelpArticle article) {
        return helpArticleRepository.save(article);
    }
}