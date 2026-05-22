package com.resolion.crm.service;

import com.resolion.crm.enums.CompanyRole;

import com.resolion.crm.enums.DealStage;
import com.resolion.crm.dto.DealRequest;
import com.resolion.crm.dto.DealResponse;
import com.resolion.crm.entity.DealEntity;
import com.resolion.crm.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealService {

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public DealResponse createDeal(String email, Long companyId, DealRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        DealEntity deal = DealEntity.builder()
                .ownerName(request.getOwnerName())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .dealName(request.getDealName())
                .amount(request.getAmount())
                .closingDate(request.getClosingDate())
                .accountName(request.getAccountName())
                .stage(request.getStage())
                .type(request.getType())
                .probability(cleanProbability(request.getProbability()))
                .nextStep(request.getNextStep())
                .leadSource(request.getLeadSource())
                .campaignSource(request.getCampaignSource())
                .contactName(request.getContactName())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .build();

        deal.calculateExpectedRevenue();

        DealEntity saved = dealRepository.save(deal);
        return toResponse(saved);
    }

    public List<DealResponse> getVisibleDeals(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<DealEntity> deals;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            deals = dealRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            deals = dealRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            deals = dealRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return deals.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DealResponse getDealById(String email, Long id) {
        DealEntity deal = dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deal not found"));

        validateAccess(email, deal.getCompanyId());

        return toResponse(deal);
    }

    public DealResponse updateDeal(String email, Long id, DealRequest request) {
        DealEntity deal = dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deal not found"));

        validateAccess(email, deal.getCompanyId());
        validateRequest(request);

        deal.setOwnerName(request.getOwnerName());
        deal.setOwnerEmail(request.getOwnerEmail());
        deal.setDealName(request.getDealName());
        deal.setAmount(request.getAmount());
        deal.setClosingDate(request.getClosingDate());
        deal.setAccountName(request.getAccountName());
        deal.setStage(request.getStage());
        deal.setType(request.getType());
        deal.setProbability(cleanProbability(request.getProbability()));
        deal.setNextStep(request.getNextStep());
        deal.setLeadSource(request.getLeadSource());
        deal.setCampaignSource(request.getCampaignSource());
        deal.setContactName(request.getContactName());
        deal.setDescription(request.getDescription());

        deal.calculateExpectedRevenue();

        DealEntity saved = dealRepository.save(deal);
        return toResponse(saved);
    }

    public void deleteDeal(String email, Long id) {
        DealEntity deal = dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deal not found"));

        validateAccess(email, deal.getCompanyId());

        dealRepository.delete(deal);
    }

    public List<DealResponse> getDealsByStage(String email, Long companyId, String stage) {
        validateAccess(email, companyId);

        return dealRepository.findByCompanyIdAndStageIgnoreCaseOrderByIdDesc(companyId, stage)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countDeals(String email, Long companyId) {
        validateAccess(email, companyId);
        return dealRepository.countByCompanyId(companyId);
    }

//    public long countDealsByStage(String email, Long companyId, String stage) {
//        validateAccess(email, companyId);
//        return dealRepository.countByCompanyIdAndStageIgnoreCase(companyId, stage);
//    }

    public List<DealResponse> getDealsByStage(String email, Long companyId, DealStage stage) {
        validateAccess(email, companyId);

        return dealRepository.findByCompanyIdAndStageOrderByIdDesc(companyId, stage)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    public long countDealsByStage(String email, Long companyId, DealStage stage) {
        validateAccess(email, companyId);
        return dealRepository.countByCompanyIdAndStage(companyId, stage);
    }


    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(DealRequest request) {
        if (request.getDealName() == null || request.getDealName().isBlank()) {
            throw new RuntimeException("Deal name is required");
        }

        if (request.getAmount() == null) {
            throw new RuntimeException("Amount is required");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Amount cannot be negative");
        }
    }

    private Integer cleanProbability(Integer probability) {
        if (probability == null) {
            return 0;
        }

        if (probability < 0) {
            return 0;
        }

        if (probability > 100) {
            return 100;
        }

        return probability;
    }

    private DealResponse toResponse(DealEntity deal) {
        return DealResponse.builder()
                .id(deal.getId())
                .ownerName(deal.getOwnerName())
                .ownerEmail(deal.getOwnerEmail())
                .dealName(deal.getDealName())
                .amount(deal.getAmount())
                .closingDate(deal.getClosingDate())
                .accountName(deal.getAccountName())
                .stage(deal.getStage())
                .type(deal.getType())
                .probability(deal.getProbability())
                .nextStep(deal.getNextStep())
                .expectedRevenue(deal.getExpectedRevenue())
                .leadSource(deal.getLeadSource())
                .campaignSource(deal.getCampaignSource())
                .contactName(deal.getContactName())
                .description(deal.getDescription())
                .companyId(deal.getCompanyId())
                .createdBy(deal.getCreatedBy())
                .createdDate(deal.getCreatedDate())
                .updatedDate(deal.getUpdatedDate())
                .build();
    }



}