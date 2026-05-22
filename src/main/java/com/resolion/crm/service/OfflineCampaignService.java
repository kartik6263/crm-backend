package com.resolion.crm.service;

import com.resolion.crm.enums.CompanyRole;
import com.resolion.crm.enums.OfflineCampaignStatus;
import com.resolion.crm.enums.OfflineCampaignType;
import com.resolion.crm.dto.OfflineCampaignRequest;
import com.resolion.crm.dto.OfflineCampaignResponse;
import com.resolion.crm.entity.OfflineCampaignEntity;
import com.resolion.crm.repository.OfflineCampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfflineCampaignService {

    @Autowired
    private OfflineCampaignRepository offlineCampaignRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public OfflineCampaignResponse createOfflineCampaign(String email,
                                                         Long companyId,
                                                         OfflineCampaignRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        OfflineCampaignEntity campaign = OfflineCampaignEntity.builder()
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

        campaign.calculateValues();

        OfflineCampaignEntity saved = offlineCampaignRepository.save(campaign);
        return toResponse(saved);
    }

    public List<OfflineCampaignResponse> getVisibleOfflineCampaigns(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<OfflineCampaignEntity> campaigns;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            campaigns = offlineCampaignRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            campaigns = offlineCampaignRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            campaigns = offlineCampaignRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return campaigns.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OfflineCampaignResponse getOfflineCampaignById(String email, Long id) {
        OfflineCampaignEntity campaign = offlineCampaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offline campaign not found"));

        validateAccess(email, campaign.getCompanyId());

        return toResponse(campaign);
    }

    public OfflineCampaignResponse updateOfflineCampaign(String email,
                                                         Long id,
                                                         OfflineCampaignRequest request) {
        OfflineCampaignEntity campaign = offlineCampaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offline campaign not found"));

        validateAccess(email, campaign.getCompanyId());
        validateRequest(request);

        campaign.setCampaignOwner(request.getCampaignOwner());
        campaign.setOwnerEmail(request.getOwnerEmail());
        campaign.setType(request.getType());
        campaign.setCampaignName(request.getCampaignName());
        campaign.setStatus(request.getStatus());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setExpectedRevenue(request.getExpectedRevenue());
        campaign.setBudgetedCost(request.getBudgetedCost());
        campaign.setActualCost(request.getActualCost());
        campaign.setExpectedResponse(request.getExpectedResponse());
        campaign.setNumbersSent(request.getNumbersSent());
        campaign.setDescription(request.getDescription());

        campaign.calculateValues();

        OfflineCampaignEntity saved = offlineCampaignRepository.save(campaign);
        return toResponse(saved);
    }

    public OfflineCampaignResponse updateStatus(String email,
                                                Long id,
                                                OfflineCampaignStatus status) {
        OfflineCampaignEntity campaign = offlineCampaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offline campaign not found"));

        validateAccess(email, campaign.getCompanyId());

        campaign.setStatus(status);

        OfflineCampaignEntity saved = offlineCampaignRepository.save(campaign);
        return toResponse(saved);
    }

    public void deleteOfflineCampaign(String email, Long id) {
        OfflineCampaignEntity campaign = offlineCampaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offline campaign not found"));

        validateAccess(email, campaign.getCompanyId());

        offlineCampaignRepository.delete(campaign);
    }

    public List<OfflineCampaignResponse> getByStatus(String email,
                                                     Long companyId,
                                                     OfflineCampaignStatus status) {
        validateAccess(email, companyId);

        return offlineCampaignRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OfflineCampaignResponse> getByType(String email,
                                                   Long companyId,
                                                   OfflineCampaignType type) {
        validateAccess(email, companyId);

        return offlineCampaignRepository.findByCompanyIdAndTypeOrderByIdDesc(companyId, type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OfflineCampaignResponse> getBetweenDates(String email,
                                                         Long companyId,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {
        validateAccess(email, companyId);

        return offlineCampaignRepository.findByCompanyIdAndStartDateBetweenOrderByStartDateAsc(
                        companyId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countOfflineCampaigns(String email, Long companyId) {
        validateAccess(email, companyId);
        return offlineCampaignRepository.countByCompanyId(companyId);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(OfflineCampaignRequest request) {
        if (request.getCampaignName() == null || request.getCampaignName().isBlank()) {
            throw new RuntimeException("Campaign name is required");
        }

        if (request.getStartDate() != null &&
                request.getEndDate() != null &&
                request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        if (request.getExpectedRevenue() != null &&
                request.getExpectedRevenue().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Expected revenue cannot be negative");
        }

        if (request.getBudgetedCost() != null &&
                request.getBudgetedCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Budgeted cost cannot be negative");
        }

        if (request.getActualCost() != null &&
                request.getActualCost().compareTo(BigDecimal.ZERO) < 0) {
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

    private OfflineCampaignResponse toResponse(OfflineCampaignEntity campaign) {
        return OfflineCampaignResponse.builder()
                .id(campaign.getId())
                .campaignOwner(campaign.getCampaignOwner())
                .ownerEmail(campaign.getOwnerEmail())
                .type(campaign.getType())
                .campaignName(campaign.getCampaignName())
                .status(campaign.getStatus())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .expectedRevenue(campaign.getExpectedRevenue())
                .budgetedCost(campaign.getBudgetedCost())
                .actualCost(campaign.getActualCost())
                .expectedProfit(campaign.getExpectedProfit())
                .costVariance(campaign.getCostVariance())
                .roiPercentage(campaign.getRoiPercentage())
                .expectedResponse(campaign.getExpectedResponse())
                .numbersSent(campaign.getNumbersSent())
                .expectedResponseRate(campaign.getExpectedResponseRate())
                .description(campaign.getDescription())
                .companyId(campaign.getCompanyId())
                .createdBy(campaign.getCreatedBy())
                .createdDate(campaign.getCreatedDate())
                .updatedDate(campaign.getUpdatedDate())
                .build();
    }
}