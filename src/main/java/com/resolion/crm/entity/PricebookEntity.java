package com.resolion.crm.entity;

import com.resolion.crm.enums.PricebookPricingModel;
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
        name = "pricebooks",
        indexes = {
                @Index(name = "idx_pricebooks_company_id", columnList = "company_id"),
                @Index(name = "idx_pricebooks_owner_email", columnList = "owner_email"),
                @Index(name = "idx_pricebooks_created_by", columnList = "created_by"),
                @Index(name = "idx_pricebooks_name", columnList = "pricebook_name"),
                @Index(name = "idx_pricebooks_active", columnList = "active")
        }
)
public class PricebookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= PRICEBOOK INFO =================

    @Column(name = "pricebook_owner", length = 150)
    private String pricebookOwner;

    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    @Column(name = "pricebook_name", nullable = false, length = 200)
    private String pricebookName;

    // Checkbox
    @Column(name = "active")
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_model", length = 80)
    private PricebookPricingModel pricingModel;

    @Column(columnDefinition = "TEXT")
    private String description;

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

        if (active == null) {
            active = true;
        }

        if (pricingModel == null) {
            pricingModel = PricebookPricingModel.NONE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}