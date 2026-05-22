package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.SolutionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolutionResponse {

    private Long id;

    private String solutionNumber;

    private String solutionOwner;
    private String ownerEmail;

    private String solutionTitle;
    private String productName;

    private SolutionStatus status;

    private String question;
    private String answer;

    private String keywords;
    private String category;

    private Boolean published;

    private Long companyId;
    private String createdBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}