package com.resolion.crm.controller;


import com.resolion.crm.dto.AnalyticsSummaryResponse;
import com.resolion.crm.enums.InvoiceStatus;
import com.resolion.crm.enums.LeadStatus;
import com.resolion.crm.enums.QuoteStage;
import com.resolion.crm.exception.AccessDeniedException;
import com.resolion.crm.repository.InvoiceRepository;
import com.resolion.crm.repository.LeadmatrixRespository;
import com.resolion.crm.repository.QuoteRepository;
import com.resolion.crm.service.CompanyAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AnalyticsController {

    private final CompanyAccessService companyAccessService;

    private final LeadmatrixRespository leadRepository;

    private final InvoiceRepository invoiceRepository;

    private final QuoteRepository quoteRepository;

    // =========================================================
    // ACCESS VALIDATION
    // =========================================================

    private void validateAccess(
            String email,
            Long companyId
    ) {

        boolean hasAccess =
                companyAccessService.hasCompanyAccess(
                        email,
                        companyId
                );

        if (!hasAccess) {

            log.warn(
                    "Unauthorized analytics access attempt by {} for company {}",
                    email,
                    companyId
            );

            throw new AccessDeniedException(
                    "You do not have access to this company"
            );
        }
    }

    // =========================================================
    // ANALYTICS SUMMARY
    // =========================================================

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> summary(
            @RequestParam String email,
            @RequestParam Long companyId
    ) {

        validateAccess(email, companyId);

        log.info(
                "Generating analytics summary for company {} by user {}",
                companyId,
                email
        );

        // =====================================================
        // LEADS
        // =====================================================

        long totalLeads =
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

        double conversionRate =
                totalLeads == 0
                        ? 0
                        : ((double) customers / totalLeads) * 100;

        // =====================================================
        // INVOICES
        // =====================================================

        long totalInvoices =
                invoiceRepository.countByCompanyId(companyId);

        long paidInvoices =
                invoiceRepository.countByCompanyIdAndStatus(
                        companyId,
                        InvoiceStatus.PAID
                );

        long unpaidInvoices =
                invoiceRepository.countByCompanyIdAndStatus(
                        companyId,
                        InvoiceStatus.UNPAID
                );

        // =====================================================
        // QUOTES
        // =====================================================

        long totalQuotes =
                quoteRepository.countByCompanyId(companyId);

        long acceptedQuotes =
                quoteRepository.countByCompanyIdAndQuoteStage(
                        companyId,
                        QuoteStage.ACCEPTED
                );

        long pendingQuotes =
                quoteRepository.countByCompanyIdAndQuoteStage(
                        companyId,
                        QuoteStage.PENDING
                );


        // =====================================================
        // RESPONSE
        // =====================================================

        AnalyticsSummaryResponse response =
                AnalyticsSummaryResponse.builder()
                        .totalLeads(totalLeads)
                        .customers(customers)
                        .lostLeads(lost)
                        .conversionRate(
                                Math.round(conversionRate * 100.0) / 100.0
                        )
                        .totalInvoices(totalInvoices)
                        .paidInvoices(paidInvoices)
                        .unpaidInvoices(unpaidInvoices)
                        .totalQuotes(totalQuotes)
                        .acceptedQuotes(acceptedQuotes)
                        .pendingQuotes(pendingQuotes)
                        .build();

        return ResponseEntity.ok(response);
    }
}