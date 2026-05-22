package com.resolion.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseAttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private Long caseId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String uploadedBy;

    private LocalDateTime uploadedDate;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;
}