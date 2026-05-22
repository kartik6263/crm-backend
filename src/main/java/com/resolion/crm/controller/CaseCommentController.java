package com.resolion.crm.controller;

import com.resolion.crm.entity.CaseCommentEntity;
import com.resolion.crm.services.CaseCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/case-comments")
public class CaseCommentController {

    @Autowired
    private CaseCommentService service;

    @PostMapping
    public CaseCommentEntity addComment(
            @RequestParam Long caseId,
            @RequestParam Long companyId,
            @RequestParam String email,
            @RequestParam String comment,
            @RequestParam(defaultValue = "false")
            Boolean internal
    ) {

        return service.addComment(
                caseId,
                companyId,
                email,
                comment,
                internal
        );
    }

    @GetMapping("/{caseId}")
    public List<CaseCommentEntity> getComments(
            @PathVariable Long caseId
    ) {

        return service.getComments(caseId);
    }
}