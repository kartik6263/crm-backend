package com.resolion.crm.controller;

import com.resolion.crm.entity.ReportFilter;
import com.resolion.crm.entity.ReportSharing;
import com.resolion.crm.dpo.AiReportRequest;
import com.resolion.crm.respository.ReportFilterRepository;
import com.resolion.crm.respository.ReportSharingRepository;

import com.resolion.crm.dpo.AiReportPlan;
import com.resolion.crm.dpo.AiReportFilterPlan;
import com.resolion.crm.services.*;

import com.resolion.crm.dpo.ReportRunResponse;
import com.resolion.crm.entity.*;
import com.resolion.crm.respository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private CompanyAccessService companyAccessService;

    @Autowired
    private ReportFolderRepository reportFolderRepository;

    @Autowired
    private UsageLimitService usageLimitService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ReportScheduleRepository reportScheduleRepository;

    @Autowired
    private ReportDefinitionRepository reportDefinitionRepository;

    @Autowired
    private AiReportService aiReportService;

    @Autowired
    private ReportFavoriteRepository reportFavoriteRepository;

    @Autowired
    private ReportRecentViewRepository reportRecentViewRepository;

    @Autowired
    private ReportFilterRepository reportFilterRepository;

    @Autowired
    private ReportSharingRepository reportSharingRepository;

    @Autowired
    private ReportRunService reportRunService;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }
    }

    private void seedDefaultFolders(Long companyId) {
        List<String> folders = List.of(
                "All Reports",
                "My Reports",
                "Favorites",
                "Recently Viewed",
                "Recently Deleted",
                "Account and Contact Reports",
                "Deal Reports",
                "Lead Reports",
                "Campaign Reports",
                "Case and Solutions Reports",
                "Product Reports",
                "Vendor Reports",
                "Quote Reports",
                "Sales Order Reports",
                "Purchase Order Reports",
                "Invoice Reports",
                "Sales Metrics Reports",
                "Email Reports",
                "Shared With Me",
                "Meeting Reports"

        );

        for (String name : folders) {
            if (reportFolderRepository.findByCompanyIdAndName(companyId, name).isEmpty()) {
                ReportFolder folder = new ReportFolder();
                folder.setCompanyId(companyId);
                folder.setName(name);
                folder.setVisible(true);
                folder.setSystemFolder(true);
                reportFolderRepository.save(folder);
            } else if ("Shared With Me".equalsIgnoreCase(folder)) {
            List<Long> sharedIds = reportSharingRepository.findByCompanyIdAndSharedWithEmail(companyId, email)
                    .stream()
                    .map(ReportSharing::getReportId)
                    .toList();

            reports = reportDefinitionRepository.findByCompanyIdAndDeletedFalseOrderByIdDesc(companyId)
                    .stream()
                    .filter(r -> sharedIds.contains(r.getId()))
                    .toList();
        }
        }
    }

    private void seedDefaultReports(Long companyId, String email) {
        if (!reportDefinitionRepository.findByCompanyIdAndDeletedFalseOrderByIdDesc(companyId).isEmpty()) {
            return;
        }

        List<ReportDefinition> reports = new ArrayList<>();

        reports.add(makeReport(companyId, "All Leads Report", "Complete list of all active leads.", "Lead Reports", "LEAD_ALL", email));
        reports.add(makeReport(companyId, "Lead Status Report", "Leads grouped by status.", "Lead Reports", "LEAD_STATUS", email));
        reports.add(makeReport(companyId, "Lead Source Report", "Leads grouped by source.", "Lead Reports", "LEAD_SOURCE", email));
        reports.add(makeReport(companyId, "My Leads Report", "Reports created by current user.", "My Reports", "MY_LEADS", email));
        reports.add(makeReport(companyId, "Sales Metrics Summary", "Core sales performance metrics.", "Sales Metrics Reports", "SALES_METRICS", email));
        reports.add(makeReport(companyId, "Meeting Report", "Upcoming and completed meetings.", "Meeting Reports", "MEETING_REPORT", email));
        reports.add(makeReport(companyId, "Email Report", "Email performance and communication history.", "Email Reports", "EMAIL_REPORT", email));
        reports.add(makeReport(companyId, "Invoice Summary", "Invoice status and value summary.", "Invoice Reports", "INVOICE_SUMMARY", email));
        reports.add(makeReport(companyId, "Quote Report", "Quote status and value summary.", "Quote Reports", "QUOTE_REPORT", email));

        reports.add(makeReport(companyId, "Customer Leads Report", "All customer leads.", "Lead Reports", "LEAD_CUSTOMERS", email));
        reports.add(makeReport(companyId, "Lost Leads Report", "All lost leads.", "Lead Reports", "LEAD_LOST", email));
        reports.add(makeReport(companyId, "Contacted Leads Report", "All contacted leads.", "Lead Reports", "LEAD_CONTACTED", email));

        reports.add(makeReport(companyId, "All Invoice Report", "List of all invoices.", "Invoice Reports", "INVOICE_ALL", email));
        reports.add(makeReport(companyId, "Invoice Summary", "Invoice status summary.", "Invoice Reports", "INVOICE_SUMMARY", email));
        reports.add(makeReport(companyId, "Quote Summary", "Quote status summary.", "Quote Reports", "QUOTE_REPORT", email));
        reports.add(makeReport(companyId, "Sales Order Summary", "Sales order status summary.", "Sales Order Reports", "SALES_ORDER_REPORT", email));
        reports.add(makeReport(companyId, "Purchase Order Summary", "Purchase order status summary.", "Purchase Order Reports", "PURCHASE_ORDER_REPORT", email));

        reportDefinitionRepository.saveAll(reports);
    }

    private ReportDefinition makeReport(Long companyId, String name, String desc, String folder, String type, String email) {
        ReportDefinition r = new ReportDefinition();
        r.setCompanyId(companyId);
        r.setReportName(name);
        r.setDescription(desc);
        r.setFolderName(folder);
        r.setReportType(type);
        r.setCreatedBy(email);
        r.setCreatedDate(LocalDateTime.now().toString());
        r.setLastAccessedDate("");
        r.setDeleted(false);
        return r;
    }

    @GetMapping("/init")
    public String initReports(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);
        seedDefaultFolders(companyId);
        seedDefaultReports(companyId, email);
        return "Reports initialized";
    }

    @GetMapping("/folders")
    public List<ReportFolder> folders(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);
        seedDefaultFolders(companyId);
        return reportFolderRepository.findByCompanyIdOrderByIdAsc(companyId);
    }

    @GetMapping("/folders/visible")
    public List<ReportFolder> visibleFolders(@RequestParam String email, @RequestParam Long companyId) {
        checkAccess(email, companyId);
        seedDefaultFolders(companyId);
        return reportFolderRepository.findByCompanyIdAndVisibleTrueOrderByIdAsc(companyId);
    }

    @PutMapping("/folders/visibility")
    public String updateFolderVisibility(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestBody List<ReportFolder> folders) {
        checkAccess(email, companyId);

        for (ReportFolder incoming : folders) {
            ReportFolder existing = reportFolderRepository.findById(incoming.getId())
                    .orElseThrow(() -> new RuntimeException("Folder not found"));

            if (!existing.getCompanyId().equals(companyId)) {
                throw new RuntimeException("Access Denied");
            }

            existing.setVisible(Boolean.TRUE.equals(incoming.getVisible()));
            reportFolderRepository.save(existing);
        }

        return "Folders updated";
    }

    @GetMapping
    public List<ReportDefinition> reports(@RequestParam String email,
                                          @RequestParam Long companyId,
                                          @RequestParam(required = false) String folder,
                                          @RequestParam(required = false) String search) {
        checkAccess(email, companyId);
        seedDefaultFolders(companyId);
        seedDefaultReports(companyId, email);

        List<ReportDefinition> reports;

        if ("My Reports".equalsIgnoreCase(folder)) {
            reports = reportDefinitionRepository.findByCompanyIdAndCreatedByAndDeletedFalseOrderByIdDesc(companyId, email);
        } else if ("Favorites".equalsIgnoreCase(folder)) {
            List<Long> favoriteIds = reportFavoriteRepository.findByCompanyIdAndUserEmail(companyId, email)
                    .stream()
                    .map(ReportFavorite::getReportId)
                    .toList();

            reports = reportDefinitionRepository.findByCompanyIdAndDeletedFalseOrderByIdDesc(companyId)
                    .stream()
                    .filter(r -> favoriteIds.contains(r.getId()))
                    .toList();
        } else if ("Recently Viewed".equalsIgnoreCase(folder)) {
            List<Long> recentIds = reportRecentViewRepository.findTop20ByCompanyIdAndUserEmailOrderByIdDesc(companyId, email)
                    .stream()
                    .map(ReportRecentView::getReportId)
                    .toList();

            reports = reportDefinitionRepository.findByCompanyIdAndDeletedFalseOrderByIdDesc(companyId)
                    .stream()
                    .filter(r -> recentIds.contains(r.getId()))
                    .toList();
        } else if ("Recently Deleted".equalsIgnoreCase(folder)) {
            reports = reportDefinitionRepository.findByCompanyIdAndDeletedTrueOrderByIdDesc(companyId);
        } else if (folder != null && !folder.isBlank() && !"All Reports".equalsIgnoreCase(folder)) {
            reports = reportDefinitionRepository.findByCompanyIdAndFolderNameAndDeletedFalseOrderByIdDesc(companyId, folder);
        } else {
            reports = reportDefinitionRepository.findByCompanyIdAndDeletedFalseOrderByIdDesc(companyId);
        }

        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            reports = reports.stream()
                    .filter(r ->
                            (r.getReportName() != null && r.getReportName().toLowerCase().contains(s)) ||
                                    (r.getDescription() != null && r.getDescription().toLowerCase().contains(s)) ||
                                    (r.getFolderName() != null && r.getFolderName().toLowerCase().contains(s))
                    )
                    .collect(Collectors.toList());
        }

        return reports;
    }

    @PostMapping
    public ReportDefinition createReport(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestBody ReportDefinition report) {
        checkAccess(email, companyId);

        report.setCompanyId(companyId);
        report.setCreatedBy(email);
        report.setCreatedDate(LocalDateTime.now().toString());
        report.setLastAccessedDate("");
        report.setDeleted(false);

        return reportDefinitionRepository.save(report);
    }

    @PostMapping("/{id}/favorite")
    public String toggleFavorite(@RequestParam String email,
                                 @RequestParam Long companyId,
                                 @PathVariable Long id) {
        checkAccess(email, companyId);

        Optional<ReportFavorite> existing =
                reportFavoriteRepository.findByCompanyIdAndUserEmailAndReportId(companyId, email, id);

        if (existing.isPresent()) {
            reportFavoriteRepository.delete(existing.get());
            return "Removed from favorites";
        }

        ReportFavorite fav = new ReportFavorite();
        fav.setCompanyId(companyId);
        fav.setUserEmail(email);
        fav.setReportId(id);
        reportFavoriteRepository.save(fav);

        return "Added to favorites";
    }

    @PostMapping("/{id}/view")
    public ReportDefinition viewReport(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @PathVariable Long id) {
        checkAccess(email, companyId);

        ReportDefinition report = reportDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        String now = LocalDateTime.now().toString();

        report.setLastAccessedDate(now);
        reportDefinitionRepository.save(report);

        ReportRecentView recent = new ReportRecentView();
        recent.setCompanyId(companyId);
        recent.setUserEmail(email);
        recent.setReportId(id);
        recent.setViewedDate(now);
        reportRecentViewRepository.save(recent);

        return report;
    }

    @DeleteMapping("/{id}")
    public String deleteReport(@RequestParam String email,
                               @RequestParam Long companyId,
                               @PathVariable Long id) {
        checkAccess(email, companyId);

        ReportDefinition report = reportDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        report.setDeleted(true);
        reportDefinitionRepository.save(report);

        return "Report moved to recently deleted";
    }

    @PostMapping("/{id}/restore")
    public String restoreReport(@RequestParam String email,
                                @RequestParam Long companyId,
                                @PathVariable Long id) {
        checkAccess(email, companyId);

        ReportDefinition report = reportDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        report.setDeleted(false);
        reportDefinitionRepository.save(report);

        return "Report restored";
    }

    @GetMapping("/advanced-analytics-link")
    public Map<String, String> advancedAnalytics(@RequestParam String email,
                                                 @RequestParam Long companyId) {
        checkAccess(email, companyId);

        Map<String, String> map = new HashMap<>();
        map.put("name", "Advanced Analytics for Resolion CRM");
        map.put("poweredBy", "Resolion Analytics");
        map.put("url", "advanced-analytics.html");
        return map;
    }




    @GetMapping("/{id}/run")
    public ReportRunResponse runReport(@RequestParam String email,
                                       @RequestParam Long companyId,
                                       @PathVariable Long id) {
        checkAccess(email, companyId);

        ReportDefinition report = reportDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        String now = LocalDateTime.now().toString();
        report.setLastAccessedDate(now);
        reportDefinitionRepository.save(report);

        ReportRecentView recent = new ReportRecentView();
        recent.setCompanyId(companyId);
        recent.setUserEmail(email);
        recent.setReportId(id);
        recent.setViewedDate(now);
        reportRecentViewRepository.save(recent);

        return reportRunService.runReport(id, companyId);
    }



    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam String email,
            @RequestParam Long companyId,
            @PathVariable Long id) {
        //////////////////////////////////////////////
        usageLimitService.checkExportLimit(companyId);
        /// ///////////////////////////////////////////

        ReportRunResponse data = reportRunService.runReport(id, companyId);

        byte[] csv = exportService.generateCSV(data);

        /// ///////////////////////
        usageLimitService.incrementExports(companyId);
        /// /////////////////////////////////////
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.csv")
                .body(csv);
    }


    @PostMapping("/schedule")
    public String scheduleReport(@RequestParam String email,
                                 @RequestParam Long companyId,
                                 @RequestBody ReportSchedule schedule) {

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }

        schedule.setCompanyId(companyId);
        schedule.setUserEmail(email);

        reportScheduleRepository.save(schedule);

        return "Report scheduled successfully";
    }




    @PostMapping("/ai-builder")
    public ReportDefinition aiReportBuilder(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @RequestBody AiReportRequest request) {

        /// //////////////////////
        usageLimitService.checkAiReportLimit(companyId);
        /// ///////////////////////////////////////
        checkAccess(email, companyId);

        String prompt = request.getPrompt() == null ? "" : request.getPrompt().toLowerCase();

        AiReportPlan plan = aiReportService.generatePlan(request.getPrompt());

        ReportDefinition report = new ReportDefinition();
        report.setCompanyId(companyId);
        report.setCreatedBy(email);
        report.setCreatedDate(LocalDateTime.now().toString());
        report.setLastAccessedDate("");
        report.setDeleted(false);
        report.setReportName(plan.getReportName());
        report.setDescription(plan.getDescription());
        report.setFolderName(plan.getFolderName());
        report.setReportType(plan.getReportType());

        ReportDefinition saved = reportDefinitionRepository.save(report);

        if (plan.getFilters() != null) {
            for (AiReportFilterPlan fp : plan.getFilters()) {
                ReportFilter filter = new ReportFilter();
                filter.setCompanyId(companyId);
                filter.setReportId(saved.getId());
                filter.setUserEmail(email);
                filter.setFilterName("AI Filter");
                filter.setFieldName(fp.getFieldName());
                filter.setOperator(fp.getOperator());
                filter.setFieldValue(fp.getFieldValue());
                filter.setCreatedDate(LocalDateTime.now().toString());
                reportFilterRepository.save(filter);
            }
        }



        if (prompt.contains("lead") && prompt.contains("status")) {
            report.setReportName("AI Lead Status Report");
            report.setDescription("Generated from prompt: " + request.getPrompt());
            report.setFolderName("Lead Reports");
            report.setReportType("LEAD_STATUS");
        } else if (prompt.contains("lead") && prompt.contains("source")) {
            report.setReportName("AI Lead Source Report");
            report.setDescription("Generated from prompt: " + request.getPrompt());
            report.setFolderName("Lead Reports");
            report.setReportType("LEAD_SOURCE");
        } else if (prompt.contains("invoice")) {
            report.setReportName("AI Invoice Summary");
            report.setDescription("Generated from prompt: " + request.getPrompt());
            report.setFolderName("Invoice Reports");
            report.setReportType("INVOICE_SUMMARY");
        } else if (prompt.contains("sales") || prompt.contains("conversion")) {
            report.setReportName("AI Sales Metrics Report");
            report.setDescription("Generated from prompt: " + request.getPrompt());
            report.setFolderName("Sales Metrics Reports");
            report.setReportType("SALES_METRICS");
        } else {
            report.setReportName("AI Custom Report");
            report.setDescription("Generated from prompt: " + request.getPrompt());
            report.setFolderName("My Reports");
            report.setReportType("CUSTOM");
        }
/// ///////////////////////////
        usageLimitService.incrementAiReports(companyId);
        /// ////////////////////////////
       // return reportDefinitionRepository.save(report);
        return saved;
    }

    @PostMapping("/{id}/filters")
    public ReportFilter saveFilter(@RequestParam String email,
                                   @RequestParam Long companyId,
                                   @PathVariable Long id,
                                   @RequestBody ReportFilter filter) {
        checkAccess(email, companyId);

        ReportDefinition report = reportDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        filter.setCompanyId(companyId);
        filter.setReportId(id);
        filter.setUserEmail(email);
        filter.setCreatedDate(LocalDateTime.now().toString());

        return reportFilterRepository.save(filter);
    }

    @GetMapping("/{id}/filters")
    public List<ReportFilter> getFilters(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @PathVariable Long id) {
        checkAccess(email, companyId);
        return reportFilterRepository.findByCompanyIdAndReportIdAndUserEmailOrderByIdDesc(companyId, id, email);
    }

    @PostMapping("/{id}/share")
    public ReportSharing shareReport(@RequestParam String email,
                                     @RequestParam Long companyId,
                                     @PathVariable Long id,
                                     @RequestBody ReportSharing sharing) {
        checkAccess(email, companyId);

        ReportDefinition report = reportDefinitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        sharing.setCompanyId(companyId);
        sharing.setReportId(id);

        if (sharing.getPermission() == null || sharing.getPermission().isBlank()) {
            sharing.setPermission("VIEW");
        }

        return reportSharingRepository.save(sharing);
    }

    @GetMapping("/shared-with-me")
    public List<ReportDefinition> sharedWithMe(@RequestParam String email,
                                               @RequestParam Long companyId) {
        checkAccess(email, companyId);

        List<Long> ids = reportSharingRepository.findByCompanyIdAndSharedWithEmail(companyId, email)
                .stream()
                .map(ReportSharing::getReportId)
                .toList();

        return reportDefinitionRepository.findByCompanyIdAndDeletedFalseOrderByIdDesc(companyId)
                .stream()
                .filter(r -> ids.contains(r.getId()))
                .toList();
    }
}