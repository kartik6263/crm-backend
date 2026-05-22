package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.ENUMS.WebinarDurationType;
import com.resolion.crm.ENUMS.WebinarStatus;
import com.resolion.crm.ENUMS.WebinarType;
import com.resolion.crm.dpo.WebinarRequest;
import com.resolion.crm.dpo.WebinarResponse;
import com.resolion.crm.entity.WebinarEntity;
import com.resolion.crm.respository.WebinarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebinarService {

    @Autowired
    private WebinarRepository webinarRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public WebinarResponse createWebinar(String email, Long companyId, WebinarRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        WebinarEntity webinar = WebinarEntity.builder()
                .webinarSchedule(request.getWebinarSchedule())
                .durationType(request.getDurationType())
                .customDurationMillis(request.getCustomDurationMillis())
                .campaignOwner(request.getCampaignOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .type(request.getType())
                .campaignName(request.getCampaignName())
                .status(request.getStatus())
                .expectedRevenue(request.getExpectedRevenue())
                .budgetedCost(request.getBudgetedCost())
                .actualCost(request.getActualCost())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .build();

        webinar.calculateFinancials();

        WebinarEntity saved = webinarRepository.save(webinar);
        return toResponse(saved);
    }

    public List<WebinarResponse> getVisibleWebinars(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<WebinarEntity> webinars;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            webinars = webinarRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            webinars = webinarRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            webinars = webinarRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return webinars.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WebinarResponse getWebinarById(String email, Long id) {
        WebinarEntity webinar = webinarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webinar not found"));

        validateAccess(email, webinar.getCompanyId());

        return toResponse(webinar);
    }

    public WebinarResponse updateWebinar(String email, Long id, WebinarRequest request) {
        WebinarEntity webinar = webinarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webinar not found"));

        validateAccess(email, webinar.getCompanyId());
        validateRequest(request);

        webinar.setWebinarSchedule(request.getWebinarSchedule());
        webinar.setDurationType(request.getDurationType());
        webinar.setCustomDurationMillis(request.getCustomDurationMillis());
        webinar.setCampaignOwner(request.getCampaignOwner());
        webinar.setOwnerEmail(request.getOwnerEmail());
        webinar.setType(request.getType());
        webinar.setCampaignName(request.getCampaignName());
        webinar.setStatus(request.getStatus());
        webinar.setExpectedRevenue(request.getExpectedRevenue());
        webinar.setBudgetedCost(request.getBudgetedCost());
        webinar.setActualCost(request.getActualCost());
        webinar.setDescription(request.getDescription());

        webinar.calculateFinancials();

        WebinarEntity saved = webinarRepository.save(webinar);
        return toResponse(saved);
    }

    public WebinarResponse updateStatus(String email, Long id, WebinarStatus status) {
        WebinarEntity webinar = webinarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webinar not found"));

        validateAccess(email, webinar.getCompanyId());

        webinar.setStatus(status);

        WebinarEntity saved = webinarRepository.save(webinar);
        return toResponse(saved);
    }

    public void deleteWebinar(String email, Long id) {
        WebinarEntity webinar = webinarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webinar not found"));

        validateAccess(email, webinar.getCompanyId());

        webinarRepository.delete(webinar);
    }

    public List<WebinarResponse> getByStatus(String email, Long companyId, WebinarStatus status) {
        validateAccess(email, companyId);

        return webinarRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WebinarResponse> getByType(String email, Long companyId, WebinarType type) {
        validateAccess(email, companyId);

        return webinarRepository.findByCompanyIdAndTypeOrderByIdDesc(companyId, type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WebinarResponse> getWebinarsBetween(String email,
                                                    Long companyId,
                                                    LocalDateTime start,
                                                    LocalDateTime end) {
        validateAccess(email, companyId);

        return webinarRepository
                .findByCompanyIdAndWebinarScheduleBetweenOrderByWebinarScheduleAsc(companyId, start, end)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countWebinars(String email, Long companyId) {
        validateAccess(email, companyId);
        return webinarRepository.countByCompanyId(companyId);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(WebinarRequest request) {
        if (request.getWebinarSchedule() == null) {
            throw new RuntimeException("Webinar schedule is required");
        }

        if (request.getCampaignName() == null || request.getCampaignName().isBlank()) {
            throw new RuntimeException("Campaign name is required");
        }

        if (request.getDurationType() == WebinarDurationType.CUSTOM) {
            if (request.getCustomDurationMillis() == null || request.getCustomDurationMillis() <= 0) {
                throw new RuntimeException("Custom duration must be greater than 0");
            }
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
    }

    private WebinarResponse toResponse(WebinarEntity webinar) {
        return WebinarResponse.builder()
                .id(webinar.getId())
                .webinarSchedule(webinar.getWebinarSchedule())
                .durationType(webinar.getDurationType())
                .durationMillis(webinar.getDurationMillis())
                .customDurationMillis(webinar.getCustomDurationMillis())
                .campaignOwner(webinar.getCampaignOwner())
                .ownerEmail(webinar.getOwnerEmail())
                .type(webinar.getType())
                .campaignName(webinar.getCampaignName())
                .status(webinar.getStatus())
                .expectedRevenue(webinar.getExpectedRevenue())
                .budgetedCost(webinar.getBudgetedCost())
                .actualCost(webinar.getActualCost())
                .expectedProfit(webinar.getExpectedProfit())
                .costVariance(webinar.getCostVariance())
                .roiPercentage(webinar.getRoiPercentage())
                .description(webinar.getDescription())
                .companyId(webinar.getCompanyId())
                .createdBy(webinar.getCreatedBy())
                .createdDate(webinar.getCreatedDate())
                .updatedDate(webinar.getUpdatedDate())
                .build();
    }
}