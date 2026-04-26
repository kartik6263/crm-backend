package com.resolion.crm.controller;

import com.resolion.crm.dpo.AnalyticsSummaryResponse;
import com.resolion.crm.respository.InvoiceRepository;
import com.resolion.crm.respository.LeadmatrixRespository;
import com.resolion.crm.respository.QuoteRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private CompanyAccessService companyAccessService;

    @Autowired
    private LeadmatrixRespository leadRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }
    }

    @GetMapping("/summary")
    public AnalyticsSummaryResponse summary(@RequestParam String email,
                                            @RequestParam Long companyId) {
        checkAccess(email, companyId);

        long totalLeads = leadRepository.countByCompanyId(companyId);
        long customers = leadRepository.countByCompanyIdAndStatus(companyId, "CUSTOMER");
        long lost = leadRepository.countByCompanyIdAndStatus(companyId, "LOST");

        double conversion = totalLeads == 0 ? 0 : ((double) customers / totalLeads) * 100;

        AnalyticsSummaryResponse res = new AnalyticsSummaryResponse();

        res.setTotalLeads(totalLeads);
        res.setCustomers(customers);
        res.setLostLeads(lost);
        res.setConversionRate(conversion);

        res.setTotalInvoices(invoiceRepository.countByCompanyId(companyId));
        res.setPaidInvoices(invoiceRepository.countByCompanyIdAndStatus(companyId, "PAID"));
        res.setUnpaidInvoices(invoiceRepository.countByCompanyIdAndStatus(companyId, "UNPAID"));

        res.setTotalQuotes(quoteRepository.countByCompanyId(companyId));
        res.setAcceptedQuotes(quoteRepository.countByCompanyIdAndStatus(companyId, "ACCEPTED"));
        res.setPendingQuotes(quoteRepository.countByCompanyIdAndStatus(companyId, "PENDING"));

        return res;
    }
}