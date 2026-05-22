package com.resolion.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private Long caseId;

    private String commentedBy;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String comment;

    private Boolean internalComment;

    private LocalDateTime createdDate;
}