package com.resolion.crm.entity;

import com.resolion.crm.enums.SolutionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "solutions",
        indexes = {
                @Index(name = "idx_solutions_company_id", columnList = "company_id"),
                @Index(name = "idx_solutions_owner_email", columnList = "owner_email"),
                @Index(name = "idx_solutions_created_by", columnList = "created_by"),
                @Index(name = "idx_solutions_number", columnList = "solution_number"),
                @Index(name = "idx_solutions_status", columnList = "status"),
                @Index(name = "idx_solutions_title", columnList = "solution_title")
        }
)
public class SolutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Read-only generated field
    @Column(name = "solution_number", nullable = false, unique = true, length = 100, updatable = false)
    private String solutionNumber;

    @Column(name = "solution_owner", length = 150)
    private String solutionOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "solution_title", nullable = false, length = 255)
    private String solutionTitle;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 80)
    private SolutionStatus status;

    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "keywords", length = 500)
    private String keywords;

    @Column(name = "category", length = 150)
    private String category;

    @Column(name = "published")
    private Boolean published;

    // ================= COMPANY / AUDIT =================

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "created_by", length = 150)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdDate == null) {
            createdDate = now;
        }

        updatedDate = now;

        if (status == null) {
            status = SolutionStatus.DRAFT;
        }

        if (published == null) {
            published = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();

        if (status == SolutionStatus.PUBLISHED) {
            published = true;
        }
    }
}