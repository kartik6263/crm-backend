package com.resolion.crm.controller;



import com.resolion.crm.dto.AiInsightResponse;
import com.resolion.crm.dto.SalesLeaderboardResponse;
import com.resolion.crm.entity.LeadmatrixEntity;
import com.resolion.crm.enums.LeadSource;
import com.resolion.crm.enums.LeadStatus;
import com.resolion.crm.repository.LeadmatrixRespository;
import com.resolion.crm.service.CompanyAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin
public class AnalyticsAdvancedController {

    private final CompanyAccessService companyAccessService;

    private final LeadmatrixRespository leadRepository;

    // =====================================================
    // ACCESS VALIDATION
    // =====================================================

    private void checkAccess(
            String email,
            Long companyId
    ) {

        boolean hasAccess =
                companyAccessService.hasCompanyAccess(
                        email,
                        companyId
                );

        if (!hasAccess) {
            throw new RuntimeException("Access Denied");
        }
    }

    // =====================================================
    // AI INSIGHTS
    // =====================================================

    @GetMapping("/insights")
    public AiInsightResponse insights(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        checkAccess(email, companyId);

        List<String> insights =
                new ArrayList<>();

        long total =
                leadRepository.countByCompanyId(companyId);

        long customers =
                leadRepository.countByCompanyIdAndStatus(
                        companyId,
                        LeadStatus.CUSTOMER
                );

        long lost =
                leadRepository.countByCompanyIdAndStatus(
                        companyId,
                        LeadStatus.LOST
                );

        long facebook =
                leadRepository.countByCompanyIdAndSource(
                        companyId,
                        LeadSource.FACEBOOK
                );

        long website =
                leadRepository.countByCompanyIdAndSource(
                        companyId,
                        LeadSource.WEBSITE
                );

        long referral =
                leadRepository.countByCompanyIdAndSource(
                        companyId,
                        LeadSource.REFERRAL
                );

        // =====================================================
        // NO DATA
        // =====================================================

        if (total == 0) {

            insights.add(
                    "No lead data available yet. Start adding leads from multiple sources."
            );

            return new AiInsightResponse(insights);
        }

        // =====================================================
        // CONVERSION RATE
        // =====================================================

        double conversion =
                ((double) customers / total) * 100;

        insights.add(
                "Current conversion rate is "
                        + String.format("%.2f", conversion)
                        + "%."
        );

        // =====================================================
        // LOST LEADS INSIGHT
        // =====================================================

        if (lost > customers) {

            insights.add(
                    "Lost leads are higher than converted customers. Improve qualification and follow-up workflows."
            );
        }

        // =====================================================
        // BEST SOURCE
        // =====================================================

        String topSource = "FACEBOOK";

        long topValue = facebook;

        if (website > topValue) {

            topSource = "WEBSITE";

            topValue = website;
        }

        if (referral > topValue) {

            topSource = "REFERRAL";

            topValue = referral;
        }

        insights.add(
                "Top performing lead source is "
                        + topSource
                        + " with "
                        + topValue
                        + " leads."
        );

        return new AiInsightResponse(insights);
    }

    // =====================================================
    // SALES FORECAST
    // =====================================================

    @GetMapping("/forecast")
    public Map<String, Object> salesForecast(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        checkAccess(email, companyId);

        long totalLeads =
                leadRepository.countByCompanyId(companyId);

        long customers =
                leadRepository.countByCompanyIdAndStatus(
                        companyId,
                        LeadStatus.CUSTOMER
                );

        double conversionRate =
                totalLeads == 0
                        ? 0
                        : ((double) customers / totalLeads);

        long predictedCustomers =
                Math.round(totalLeads * conversionRate);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("totalLeads", totalLeads);

        response.put("currentCustomers", customers);

        response.put(
                "conversionRate",
                Math.round(conversionRate * 10000.0) / 100.0
        );

        response.put(
                "predictedCustomersNextCycle",
                predictedCustomers
        );

        return response;
    }

    // =====================================================
    // SALES LEADERBOARD
    // =====================================================

    @GetMapping("/sales-leaderboard")
    public List<SalesLeaderboardResponse> salesLeaderboard(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        checkAccess(email, companyId);

        List<LeadmatrixEntity> assignedLeads =
                leadRepository.findByCompanyIdAndAssignedToIsNotNull(
                        companyId
                );

        List<String> salesEmails =
                assignedLeads.stream()
                        .map(LeadmatrixEntity::getAssignedTo)
                        .filter(Objects::nonNull)
                        .filter(e -> !e.isBlank())
                        .distinct()
                        .collect(Collectors.toList());

        List<SalesLeaderboardResponse> leaderboard =
                new ArrayList<>();

        for (String salesEmail : salesEmails) {

            long assigned =
                    leadRepository.countByCompanyIdAndAssignedTo(
                            companyId,
                            salesEmail
                    );

            long customers =
                    leadRepository
                            .countByCompanyIdAndAssignedToAndStatus(
                                    companyId,
                                    salesEmail,
                                    LeadStatus.CUSTOMER
                            );

            double conversion =
                    assigned == 0
                            ? 0
                            : ((double) customers / assigned) * 100;

            leaderboard.add(
                    new SalesLeaderboardResponse(
                            salesEmail,
                            assigned,
                            customers,
                            Math.round(conversion * 100.0) / 100.0
                    )
            );
        }

        leaderboard.sort(
                Comparator.comparingLong(
                        SalesLeaderboardResponse::getCustomers
                ).reversed()
        );

        return leaderboard;
    }
}