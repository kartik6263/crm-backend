package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.ENUMS.PricebookPricingModel;
import com.resolion.crm.dpo.PricebookRequest;
import com.resolion.crm.dpo.PricebookResponse;
import com.resolion.crm.entity.PricebookEntity;
import com.resolion.crm.respository.PricebookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PricebookService {

    @Autowired
    private PricebookRepository pricebookRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public PricebookResponse createPricebook(String email, Long companyId, PricebookRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        if (pricebookRepository.existsByCompanyIdAndPricebookNameIgnoreCase(companyId, request.getPricebookName())) {
            throw new RuntimeException("Pricebook name already exists");
        }

        PricebookEntity pricebook = PricebookEntity.builder()
                .pricebookOwner(request.getPricebookOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .pricebookName(request.getPricebookName())
                .active(request.getActive())
                .pricingModel(request.getPricingModel())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .build();

        PricebookEntity saved = pricebookRepository.save(pricebook);
        return toResponse(saved);
    }

    public List<PricebookResponse> getVisiblePricebooks(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<PricebookEntity> pricebooks;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            pricebooks = pricebookRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            pricebooks = pricebookRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            pricebooks = pricebookRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return pricebooks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PricebookResponse getPricebookById(String email, Long id) {
        PricebookEntity pricebook = pricebookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricebook not found"));

        validateAccess(email, pricebook.getCompanyId());

        return toResponse(pricebook);
    }

    public PricebookResponse updatePricebook(String email, Long id, PricebookRequest request) {
        PricebookEntity pricebook = pricebookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricebook not found"));

        validateAccess(email, pricebook.getCompanyId());
        validateRequest(request);

        pricebookRepository.findByCompanyIdAndPricebookNameIgnoreCase(
                pricebook.getCompanyId(),
                request.getPricebookName()
        ).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Pricebook name already exists");
            }
        });

        pricebook.setPricebookOwner(request.getPricebookOwner());
        pricebook.setOwnerEmail(request.getOwnerEmail());
        pricebook.setPricebookName(request.getPricebookName());
        pricebook.setActive(request.getActive());
        pricebook.setPricingModel(request.getPricingModel());
        pricebook.setDescription(request.getDescription());

        PricebookEntity saved = pricebookRepository.save(pricebook);
        return toResponse(saved);
    }

    public void deletePricebook(String email, Long id) {
        PricebookEntity pricebook = pricebookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricebook not found"));

        validateAccess(email, pricebook.getCompanyId());

        pricebookRepository.delete(pricebook);
    }

    public PricebookResponse updateActiveStatus(String email, Long id, Boolean active) {
        PricebookEntity pricebook = pricebookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricebook not found"));

        validateAccess(email, pricebook.getCompanyId());

        pricebook.setActive(active);

        PricebookEntity saved = pricebookRepository.save(pricebook);
        return toResponse(saved);
    }

    public List<PricebookResponse> getActivePricebooks(String email, Long companyId) {
        validateAccess(email, companyId);

        return pricebookRepository.findByCompanyIdAndActiveOrderByIdDesc(companyId, true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PricebookResponse> getByPricingModel(String email,
                                                     Long companyId,
                                                     PricebookPricingModel pricingModel) {
        validateAccess(email, companyId);

        return pricebookRepository.findByCompanyIdAndPricingModelOrderByIdDesc(companyId, pricingModel)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countPricebooks(String email, Long companyId) {
        validateAccess(email, companyId);
        return pricebookRepository.countByCompanyId(companyId);
    }

    public long countActivePricebooks(String email, Long companyId) {
        validateAccess(email, companyId);
        return pricebookRepository.countByCompanyIdAndActive(companyId, true);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(PricebookRequest request) {
        if (request.getPricebookName() == null || request.getPricebookName().isBlank()) {
            throw new RuntimeException("Pricebook name is required");
        }

        if (request.getActive() == null) {
            request.setActive(true);
        }

        if (request.getPricingModel() == null) {
            request.setPricingModel(PricebookPricingModel.NONE);
        }
    }

    private PricebookResponse toResponse(PricebookEntity pricebook) {
        return PricebookResponse.builder()
                .id(pricebook.getId())
                .pricebookOwner(pricebook.getPricebookOwner())
                .ownerEmail(pricebook.getOwnerEmail())
                .pricebookName(pricebook.getPricebookName())
                .active(pricebook.getActive())
                .pricingModel(pricebook.getPricingModel())
                .description(pricebook.getDescription())
                .companyId(pricebook.getCompanyId())
                .createdBy(pricebook.getCreatedBy())
                .createdDate(pricebook.getCreatedDate())
                .updatedDate(pricebook.getUpdatedDate())
                .build();
    }
}