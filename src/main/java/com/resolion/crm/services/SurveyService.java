package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.ENUMS.SurveyCampaignType;
import com.resolion.crm.ENUMS.SurveyStatus;
import com.resolion.crm.dpo.SurveyRequest;
import com.resolion.crm.dpo.SurveyResponse;
import com.resolion.crm.entity.SurveyEntity;
import com.resolion.crm.respository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public SurveyResponse createSurvey(String email, Long companyId, SurveyRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        SurveyEntity survey = SurveyEntity.builder()
                .surveyDepartment(request.getSurveyDepartment())
                .surveyType(request.getSurveyType())
                .survey(request.getSurvey())
                .campaignOwner(request.getCampaignOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .type(request.getType())
                .campaignName(request.getCampaignName())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .expectedRevenue(request.getExpectedRevenue())
                .budgetedCost(request.getBudgetedCost())
                .actualCost(request.getActualCost())
                .expectedResponse(request.getExpectedResponse())
                .numbersSent(request.getNumbersSent())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .build();

        survey.calculateValues();

        SurveyEntity saved = surveyRepository.save(survey);
        return toResponse(saved);
    }

    public List<SurveyResponse> getVisibleSurveys(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<SurveyEntity> surveys;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            surveys = surveyRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            surveys = surveyRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            surveys = surveyRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return surveys.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SurveyResponse getSurveyById(String email, Long id) {
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        validateAccess(email, survey.getCompanyId());

        return toResponse(survey);
    }

    public SurveyResponse updateSurvey(String email, Long id, SurveyRequest request) {
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        validateAccess(email, survey.getCompanyId());
        validateRequest(request);

        survey.setSurveyDepartment(request.getSurveyDepartment());
        survey.setSurveyType(request.getSurveyType());
        survey.setSurvey(request.getSurvey());
        survey.setCampaignOwner(request.getCampaignOwner());
        survey.setOwnerEmail(request.getOwnerEmail());
        survey.setType(request.getType());
        survey.setCampaignName(request.getCampaignName());
        survey.setStatus(request.getStatus());
        survey.setStartDate(request.getStartDate());
        survey.setEndDate(request.getEndDate());
        survey.setExpectedRevenue(request.getExpectedRevenue());
        survey.setBudgetedCost(request.getBudgetedCost());
        survey.setActualCost(request.getActualCost());
        survey.setExpectedResponse(request.getExpectedResponse());
        survey.setNumbersSent(request.getNumbersSent());
        survey.setDescription(request.getDescription());

        survey.calculateValues();

        SurveyEntity saved = surveyRepository.save(survey);
        return toResponse(saved);
    }

    public SurveyResponse updateStatus(String email, Long id, SurveyStatus status) {
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        validateAccess(email, survey.getCompanyId());

        survey.setStatus(status);

        SurveyEntity saved = surveyRepository.save(survey);
        return toResponse(saved);
    }

    public void deleteSurvey(String email, Long id) {
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        validateAccess(email, survey.getCompanyId());

        surveyRepository.delete(survey);
    }

    public List<SurveyResponse> getByStatus(String email, Long companyId, SurveyStatus status) {
        validateAccess(email, companyId);

        return surveyRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SurveyResponse> getByType(String email, Long companyId, SurveyCampaignType type) {
        validateAccess(email, companyId);

        return surveyRepository.findByCompanyIdAndTypeOrderByIdDesc(companyId, type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SurveyResponse> getSurveysBetween(String email,
                                                  Long companyId,
                                                  LocalDate startDate,
                                                  LocalDate endDate) {
        validateAccess(email, companyId);

        return surveyRepository.findByCompanyIdAndStartDateBetweenOrderByStartDateAsc(
                        companyId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countSurveys(String email, Long companyId) {
        validateAccess(email, companyId);
        return surveyRepository.countByCompanyId(companyId);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(SurveyRequest request) {
        if (request.getCampaignName() == null || request.getCampaignName().isBlank()) {
            throw new RuntimeException("Campaign name is required");
        }

        if (request.getStartDate() != null && request.getEndDate() != null &&
                request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        if (request.getExpectedRevenue() != null && request.getExpectedRevenue().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Expected revenue cannot be negative");
        }

        if (request.getBudgetedCost() != null && request.getBudgetedCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Budgeted cost cannot be negative");
        }

        if (request.getActualCost() != null && request.getActualCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Actual cost cannot be negative");
        }

        if (request.getExpectedResponse() != null && request.getExpectedResponse() < 0) {
            throw new RuntimeException("Expected response cannot be negative");
        }

        if (request.getNumbersSent() != null && request.getNumbersSent() < 0) {
            throw new RuntimeException("Numbers sent cannot be negative");
        }

        if (request.getExpectedResponse() != null &&
                request.getNumbersSent() != null &&
                request.getNumbersSent() > 0 &&
                request.getExpectedResponse() > request.getNumbersSent()) {
            throw new RuntimeException("Expected response cannot be greater than numbers sent");
        }
    }

    private SurveyResponse toResponse(SurveyEntity survey) {
        return SurveyResponse.builder()
                .id(survey.getId())
                .surveyDepartment(survey.getSurveyDepartment())
                .surveyType(survey.getSurveyType())
                .survey(survey.getSurvey())
                .campaignOwner(survey.getCampaignOwner())
                .ownerEmail(survey.getOwnerEmail())
                .type(survey.getType())
                .campaignName(survey.getCampaignName())
                .status(survey.getStatus())
                .startDate(survey.getStartDate())
                .endDate(survey.getEndDate())
                .expectedRevenue(survey.getExpectedRevenue())
                .budgetedCost(survey.getBudgetedCost())
                .actualCost(survey.getActualCost())
                .expectedProfit(survey.getExpectedProfit())
                .costVariance(survey.getCostVariance())
                .roiPercentage(survey.getRoiPercentage())
                .expectedResponse(survey.getExpectedResponse())
                .numbersSent(survey.getNumbersSent())
                .expectedResponseRate(survey.getExpectedResponseRate())
                .description(survey.getDescription())
                .companyId(survey.getCompanyId())
                .createdBy(survey.getCreatedBy())
                .createdDate(survey.getCreatedDate())
                .updatedDate(survey.getUpdatedDate())
                .build();
    }
}