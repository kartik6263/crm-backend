package com.resolion.crm.controller;

import com.resolion.crm.dpo.SalesLeaderboardResponse;
import com.resolion.crm.entity.LeadmatrixEntity;
import java.util.stream.Collectors;
import com.resolion.crm.dpo.AiInsightResponse;
import com.resolion.crm.dpo.ChartResponse;
import com.resolion.crm.respository.InvoiceRepository;
import com.resolion.crm.respository.LeadmatrixRespository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/analytics/advanced")
public class AnalyticsAdvancedController {

    @Autowired
    private CompanyAccessService companyAccessService;

    @Autowired
    private LeadmatrixRespository leadRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }
    }

    @GetMapping("/lead-status-chart")
    public ChartResponse leadStatusChart(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);

        List<String> labels = List.of("NEW", "CONTACTED", "QUALIFIED", "CUSTOMER", "LOST");
        List<Long> values = new ArrayList<>();

        for (String status : labels) {
            values.add(leadRepository.countByCompanyIdAndStatus(companyId, status));
        }

        return new ChartResponse(labels, values);
    }

    @GetMapping("/lead-source-chart")
    public ChartResponse leadSourceChart(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);

        List<String> labels = List.of("Facebook", "Website", "Referral");
        List<Long> values = new ArrayList<>();

        for (String source : labels) {
            values.add(leadRepository.countByCompanyIdAndSource(companyId, source));
        }

        return new ChartResponse(labels, values);
    }

    @GetMapping("/invoice-status-chart")
    public ChartResponse invoiceStatusChart(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);

        List<String> labels = List.of("PAID", "UNPAID", "OVERDUE");
        List<Long> values = new ArrayList<>();

        for (String status : labels) {
            values.add(invoiceRepository.countByCompanyIdAndStatus(companyId, status));
        }

        return new ChartResponse(labels, values);
    }

    @GetMapping("/forecast")
    public Map<String, Object> salesForecast(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);

        long totalLeads = leadRepository.countByCompanyId(companyId);
        long customers = leadRepository.countByCompanyIdAndStatus(companyId, "CUSTOMER");

        double conversionRate = totalLeads == 0 ? 0 : ((double) customers / totalLeads);
        long predictedCustomersNextMonth = Math.round(totalLeads * conversionRate * 1.15);

        Map<String, Object> map = new HashMap<>();
        map.put("currentConversionRate", String.format("%.2f%%", conversionRate * 100));
        map.put("predictedCustomersNextMonth", predictedCustomersNextMonth);
        map.put("forecastNote", "Prediction is based on current conversion trend with 15% growth assumption.");

        return map;
    }

    @GetMapping("/insights")
    public AiInsightResponse insights(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);

        List<String> insights = new ArrayList<>();

        long total = leadRepository.countByCompanyId(companyId);
        long customers = leadRepository.countByCompanyIdAndStatus(companyId, "CUSTOMER");
        long lost = leadRepository.countByCompanyIdAndStatus(companyId, "LOST");

        long facebook = leadRepository.countByCompanyIdAndSource(companyId, "Facebook");
        long website = leadRepository.countByCompanyIdAndSource(companyId, "Website");
        long referral = leadRepository.countByCompanyIdAndSource(companyId, "Referral");

        if (total == 0) {
            insights.add("No lead data available yet. Start by adding leads from different sources.");
        } else {
            double conversion = ((double) customers / total) * 100;
            insights.add("Your current conversion rate is " + String.format("%.2f", conversion) + "%.");

            if (lost > customers) {
                insights.add("Lost leads are higher than customer leads. Improve follow-up speed and qualification process.");
            }
            String topSource = "Facebook";
            long topValue = facebook;

            if (website > topValue) {
                topSource = "Website";
                topValue = website;
            }
            if (referral > topValue) {
                topSource = "Referral";
                topValue = referral;
            }
            insights.add("Top lead source is " + topSource + " with " + topValue + " leads.");
        }
        return new AiInsightResponse(insights);
    }


    @GetMapping("/sales-leaderboard")
    public List<SalesLeaderboardResponse> salesLeaderboard(@RequestParam String email,
                                                           @RequestParam Long companyId) {
        checkAccess(email, companyId);

        List<LeadmatrixEntity> assignedLeads = leadRepository.findByCompanyIdAndAssignedToIsNotNull(companyId);

        List<String> salesEmails = assignedLeads.stream()
                .map(LeadmatrixEntity::getAssignedTo)
                .filter(e -> e != null && !e.isBlank())
                .distinct()
                .collect(Collectors.toList());

        List<SalesLeaderboardResponse> leaderboard = new ArrayList<>();

        for (String salesEmail : salesEmails) {
            long assigned = leadRepository.countByCompanyIdAndAssignedTo(companyId, salesEmail);
            long customers = leadRepository.countByCompanyIdAndAssignedToAndStatus(companyId, salesEmail, "CUSTOMER");

            double conversion = assigned == 0 ? 0 : ((double) customers / assigned) * 100;

            leaderboard.add(new SalesLeaderboardResponse(
                    salesEmail,
                    assigned,
                    customers,
                    conversion
            ));
        }
        leaderboard.sort((a, b) -> Long.compare(b.getCustomers(), a.getCustomers()));
        return leaderboard;
    }
}