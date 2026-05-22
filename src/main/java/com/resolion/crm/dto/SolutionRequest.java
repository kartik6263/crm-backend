package com.resolion.crm.dto;

import com.resolion.crm.enums.SolutionStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolutionRequest {

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
}