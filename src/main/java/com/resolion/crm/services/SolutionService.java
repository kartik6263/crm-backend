package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.ENUMS.SolutionStatus;
import com.resolion.crm.dpo.SolutionRequest;
import com.resolion.crm.dpo.SolutionResponse;
import com.resolion.crm.entity.SolutionEntity;
import com.resolion.crm.respository.SolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SolutionService {

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public SolutionResponse createSolution(String email, Long companyId, SolutionRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        SolutionEntity solution = SolutionEntity.builder()
                .solutionNumber(generateSolutionNumber())
                .solutionOwner(request.getSolutionOwner())
                .ownerEmail(request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()
                        ? email
                        : request.getOwnerEmail())
                .solutionTitle(request.getSolutionTitle())
                .productName(request.getProductName())
                .status(request.getStatus())
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .keywords(request.getKeywords())
                .category(request.getCategory())
                .published(request.getPublished())
                .companyId(companyId)
                .createdBy(email)
                .build();

        applyPublishLogic(solution);

        SolutionEntity saved = solutionRepository.save(solution);
        return toResponse(saved);
    }

    public List<SolutionResponse> getVisibleSolutions(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<SolutionEntity> solutions;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            solutions = solutionRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            solutions = solutionRepository.findByCompanyIdAndOwnerEmailOrderByIdDesc(companyId, email);
        } else {
            solutions = solutionRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return solutions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SolutionResponse getSolutionById(String email, Long id) {
        SolutionEntity solution = solutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solution not found"));

        validateAccess(email, solution.getCompanyId());

        return toResponse(solution);
    }

    public SolutionResponse updateSolution(String email, Long id, SolutionRequest request) {
        SolutionEntity solution = solutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solution not found"));

        validateAccess(email, solution.getCompanyId());
        validateRequest(request);

        // solutionNumber intentionally not updated because read-only
        solution.setSolutionOwner(request.getSolutionOwner());
        solution.setOwnerEmail(request.getOwnerEmail());
        solution.setSolutionTitle(request.getSolutionTitle());
        solution.setProductName(request.getProductName());
        solution.setStatus(request.getStatus());
        solution.setQuestion(request.getQuestion());
        solution.setAnswer(request.getAnswer());
        solution.setKeywords(request.getKeywords());
        solution.setCategory(request.getCategory());
        solution.setPublished(request.getPublished());

        applyPublishLogic(solution);

        SolutionEntity saved = solutionRepository.save(solution);
        return toResponse(saved);
    }

    public SolutionResponse updateStatus(String email, Long id, SolutionStatus status) {
        SolutionEntity solution = solutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solution not found"));

        validateAccess(email, solution.getCompanyId());

        solution.setStatus(status);
        applyPublishLogic(solution);

        SolutionEntity saved = solutionRepository.save(solution);
        return toResponse(saved);
    }

    public void deleteSolution(String email, Long id) {
        SolutionEntity solution = solutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solution not found"));

        validateAccess(email, solution.getCompanyId());

        solutionRepository.delete(solution);
    }

    public List<SolutionResponse> getByStatus(String email, Long companyId, SolutionStatus status) {
        validateAccess(email, companyId);

        return solutionRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SolutionResponse> getPublishedSolutions(String email, Long companyId) {
        validateAccess(email, companyId);

        return solutionRepository.findByCompanyIdAndPublishedOrderByIdDesc(companyId, true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SolutionResponse> searchSolutions(String email, Long companyId, String keyword) {
        validateAccess(email, companyId);

        return solutionRepository.searchByKeyword(companyId, keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countSolutions(String email, Long companyId) {
        validateAccess(email, companyId);
        return solutionRepository.countByCompanyId(companyId);
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(SolutionRequest request) {
        if (request.getSolutionTitle() == null || request.getSolutionTitle().isBlank()) {
            throw new RuntimeException("Solution title is required");
        }

        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new RuntimeException("Question is required");
        }

        if (request.getAnswer() == null || request.getAnswer().isBlank()) {
            throw new RuntimeException("Answer is required");
        }
    }

    private void applyPublishLogic(SolutionEntity solution) {
        if (solution.getStatus() == null) {
            solution.setStatus(SolutionStatus.DRAFT);
        }

        if (solution.getPublished() == null) {
            solution.setPublished(false);
        }

        if (solution.getStatus() == SolutionStatus.PUBLISHED) {
            solution.setPublished(true);
        }

        if (solution.getPublished()) {
            solution.setStatus(SolutionStatus.PUBLISHED);
        }
    }

    private String generateSolutionNumber() {
        String number;

        do {
            int random = 100000 + new Random().nextInt(900000);
            number = "SOL-" + random;
        } while (solutionRepository.existsBySolutionNumberIgnoreCase(number));

        return number;
    }

    private SolutionResponse toResponse(SolutionEntity solution) {
        return SolutionResponse.builder()
                .id(solution.getId())
                .solutionNumber(solution.getSolutionNumber())
                .solutionOwner(solution.getSolutionOwner())
                .ownerEmail(solution.getOwnerEmail())
                .solutionTitle(solution.getSolutionTitle())
                .productName(solution.getProductName())
                .status(solution.getStatus())
                .question(solution.getQuestion())
                .answer(solution.getAnswer())
                .keywords(solution.getKeywords())
                .category(solution.getCategory())
                .published(solution.getPublished())
                .companyId(solution.getCompanyId())
                .createdBy(solution.getCreatedBy())
                .createdDate(solution.getCreatedDate())
                .updatedDate(solution.getUpdatedDate())
                .build();
    }
}