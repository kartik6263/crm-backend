package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CampaignStatus;
import com.resolion.crm.ENUMS.CampaignType;
import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.dpo.CampaignRequest;
import com.resolion.crm.dpo.CampaignResponse;
import com.resolion.crm.entity.CampaignEntity;
import com.resolion.crm.respository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public CampaignResponse createCampaign(String email, Long companyId, CampaignRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        CampaignEntity campaign = CampaignEntity.builder()
                .campaignSubject(request.getCampaignSubject())
                .senderName(request.getSenderName())
                .senderAddress(request.getSenderAddress())
                .replyToAddress(request.getReplyToAddress())
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

        campaign.calculateFinancials();

        CampaignEntity saved = campaignRepository.save(campaign);
        return toResponse(saved);
    }

    public List<CampaignResponse> getVisibleCampaigns(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<CampaignEntity> campaigns;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            campaigns = campaignRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            campaigns = campaignRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            campaigns = campaignRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return campaigns.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CampaignResponse getCampaignById(String email, Long id) {
        CampaignEntity campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        validateAccess(email, campaign.getCompanyId());

        return toResponse(campaign);
    }

    public CampaignResponse updateCampaign(String email, Long id, CampaignRequest request) {
        CampaignEntity campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        validateAccess(email, campaign.getCompanyId());
        validateRequest(request);

        campaign.setCampaignSubject(request.getCampaignSubject());
        campaign.setSenderName(request.getSenderName());
        campaign.setSenderAddress(request.getSenderAddress());
        campaign.setReplyToAddress(request.getReplyToAddress());
        campaign.setCampaignOwner(request.getCampaignOwner());
        campaign.setOwnerEmail(request.getOwnerEmail());
        campaign.setType(request.getType());
        campaign.setCampaignName(request.getCampaignName());
        campaign.setStatus(request.getStatus());
        campaign.setExpectedRevenue(request.getExpectedRevenue());
        campaign.setBudgetedCost(request.getBudgetedCost());
        campaign.setActualCost(request.getActualCost());
        campaign.setDescription(request.getDescription());

        campaign.calculateFinancials();

        CampaignEntity saved = campaignRepository.save(campaign);
        return toResponse(saved);
    }

    public CampaignResponse updateStatus(String email, Long id, CampaignStatus status) {
        CampaignEntity campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        validateAccess(email, campaign.getCompanyId());

        campaign.setStatus(status);

        CampaignEntity saved = campaignRepository.save(campaign);
        return toResponse(saved);
    }

    public void deleteCampaign(String email, Long id) {
        CampaignEntity campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        validateAccess(email, campaign.getCompanyId());

        campaignRepository.delete(campaign);
    }

    public List<CampaignResponse> getByStatus(String email, Long companyId, CampaignStatus status) {
        validateAccess(email, companyId);

        return campaignRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CampaignResponse> getByType(String email, Long companyId, CampaignType type) {
        validateAccess(email, companyId);

        return campaignRepository.findByCompanyIdAndTypeOrderByIdDesc(companyId, type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CampaignResponse> searchCampaigns(String email, Long companyId, String keyword) {
        validateAccess(email, companyId);

        return campaignRepository.searchByKeyword(companyId, keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countCampaigns(String email, Long companyId) {
        validateAccess(email, companyId);
        return campaignRepository.countByCompanyId(companyId);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(CampaignRequest request) {
        if (request.getCampaignName() == null || request.getCampaignName().isBlank()) {
            throw new RuntimeException("Campaign name is required");
        }

        if (request.getCampaignSubject() == null || request.getCampaignSubject().isBlank()) {
            throw new RuntimeException("Campaign subject is required");
        }

        validateEmail(request.getSenderAddress(), "Sender address");
        validateEmail(request.getReplyToAddress(), "Reply-to address");

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

    private void validateEmail(String email, String fieldName) {
        if (email == null || email.isBlank()) {
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new RuntimeException(fieldName + " is invalid");
        }
    }

    private CampaignResponse toResponse(CampaignEntity campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .campaignSubject(campaign.getCampaignSubject())
                .senderName(campaign.getSenderName())
                .senderAddress(campaign.getSenderAddress())
                .replyToAddress(campaign.getReplyToAddress())
                .campaignOwner(campaign.getCampaignOwner())
                .ownerEmail(campaign.getOwnerEmail())
                .type(campaign.getType())
                .campaignName(campaign.getCampaignName())
                .status(campaign.getStatus())
                .expectedRevenue(campaign.getExpectedRevenue())
                .budgetedCost(campaign.getBudgetedCost())
                .actualCost(campaign.getActualCost())
                .expectedProfit(campaign.getExpectedProfit())
                .costVariance(campaign.getCostVariance())
                .roiPercentage(campaign.getRoiPercentage())
                .description(campaign.getDescription())
                .companyId(campaign.getCompanyId())
                .createdBy(campaign.getCreatedBy())
                .createdDate(campaign.getCreatedDate())
                .updatedDate(campaign.getUpdatedDate())
                .build();
    }
}