package com.resolion.crm.controller;



import com.resolion.crm.ENUMS.*;
import com.resolion.crm.entity.LeadActivity;
import com.resolion.crm.entity.LeadmatrixEntity;
import com.resolion.crm.entity.databaseCRM;
import com.resolion.crm.respository.ActivityRepository;
import com.resolion.crm.respository.LeadmatrixRespository;
import com.resolion.crm.services.CompanyAccessService;
import com.resolion.crm.services.EmailService;
import com.resolion.crm.services.UsageLimitService;
import com.resolion.crm.services.crmService;
import com.resolion.crm.services.leadServices;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.resolion.crm.entity.*;
import com.resolion.crm.respository.*;
import com.resolion.crm.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class LeadMatrixController {


    @Autowired
    private leadServices leadServices;

    @Autowired
    crmService crmService;

    @Autowired
    private UsageLimitService usageLimitService;


    @Autowired
    private crmRespository crmRespository;

    @Autowired
    CompanyAccessService companyAccessService;

    @Autowired
    private LeadmatrixRespository leadmatrixRepository;

    @Autowired
    private LeadNoteRepository leadNoteRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ExcelLeadService excelLeadService;

//    @Autowired
//    private TwilioService twilioService;

    public LeadMatrixController(leadServices leadServices,
                                crmService crmService,
                                CompanyAccessService companyAccessService,
                                LeadmatrixRespository leadmatrixRepository) {
        this.leadServices = leadServices;
        this.crmService = crmService;
        this.companyAccessService = companyAccessService;
        this.leadmatrixRepository = leadmatrixRepository;
    }


    // updatelead status()
    private void saveActivity(Long leadId, String type, String description) {
        LeadActivity activity = new LeadActivity();
        activity.setLeadId(leadId);
        activity.setActivityType(type);
        activity.setDescription(description);
        activity.setActivityDate(java.time.LocalDate.now().toString());
        activityRepository.save(activity);
    }
       private boolean hasAccess(String email, Long companyId) {
           return companyAccessService.hasCompanyAccess(email, companyId);
        }


        private ResponseEntity<?> forbidden() {
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Access denied"
            ));
        }


        // =============================== CREATE LEAD ========================================

   // @PostMapping("/add")
    //public ResponseEntity<?> createLead(@RequestBody LeadmatrixEntity lead) {
    @PostMapping("/add")
    public ResponseEntity<?> createLead(@RequestParam String email, @RequestParam Long companyId, @RequestBody LeadmatrixEntity lead) {
        //databaseCRM user = crmService.getUserByEmail(email);

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            return ResponseEntity.status(403).body("No company access");
        }
/// /////
        if (!hasAccess(email, companyId)) {
            return forbidden();
        }
        databaseCRM user = crmService.getUserByEmail(email);
/// ///
        // validation
//        if (lead.getName() == null || lead.getName().isBlank()) {
//            return ResponseEntity.badRequest().body("Name is required");
//        }
        if (lead.getFirstName() == null || lead.getFirstName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "First name is required"));
        }

        if (lead.getLastName() == null || lead.getLastName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Last name is required"));
        }

        if (lead.getCompany() == null || lead.getCompany().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Company is required"));
        }

        if (lead.getPhone() == null || lead.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Phone is required"));
        }

        if (lead.getEmail() != null && !lead.getEmail().isBlank() && !lead.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email"));
        }

        usageLimitService.checkLeadLimit(companyId);
        lead.setCompanyId(companyId);
        lead.setCreatedBy(user.getEmail());

        if (lead.getAssignedTo() == null || lead.getAssignedTo().isBlank()) {
            lead.setAssignedTo(user.getEmail());
        }


//        if (lead.getPhone() == null || lead.getPhone().isBlank()) {
//            return ResponseEntity.badRequest().body("Phone is required");
//        }
//
//        if (lead.getEmail() != null && !lead.getEmail().isBlank() && !lead.getEmail().contains("@")) {
//            return ResponseEntity.badRequest().body("Invalid email");
//        }

//        if (lead.getStatus() == null || lead.getStatus().isBlank()) {
//            lead.setStatus("NEW");
//        }
//
//        if (lead.getSource() == null || lead.getSource().isBlank()) {
//            lead.setSource("Unknown");
//        }

        if (lead.getStatus() == null) {
            lead.setStatus(LeadStatus.NEW);
        }

        if (lead.getSource() == null) {
            lead.setSource(LeadSource.NONE);
        }

        if (lead.getIndustry() == null) {
            lead.setIndustry(LeadIndustry.NONE);
        }

        if (lead.getRating() == null) {
            lead.setRating(LeadRating.NONE);
        }

        if (lead.getCreatedDate() == null || lead.getCreatedDate().isBlank()) {
            lead.setCreatedDate(LocalDate.now().toString());
        }

        // defaults
        lead.setStatus(LeadStatus.NEW);
        lead.setCreatedDate(LocalDate.now().toString());

        // ✅ AUTO companyId assign
        //lead.setCompanyId(user.getCompanyId());
        lead.setCompanyId(companyId);
        lead.setCreatedBy(user.getEmail());

        // OPTIONAL (best practice)
        lead.setAssignedTo(user.getEmail());
/// ///////////////////
        usageLimitService.checkLeadLimit(companyId);
/// ////////////////////////////////////

        LeadmatrixEntity saved = leadServices.saveLead(lead);

        saveActivity(saved.getId(), "LEAD_CREATED", "Lead created: " + saved.getFullName());


        if (saved.getAssignedTo() != null && !saved.getAssignedTo().isBlank()) {
            emailService.sendEmail(
                    saved.getAssignedTo(),
                    "New Lead Assigned",
                    "A new lead has been assigned to you: " + saved.getFullName()
            );
            saveActivity(saved.getId(), "LEAD_ASSIGNED", "Lead assigned to: " + saved.getAssignedTo());
        }
/// /////////////
        usageLimitService.incrementLeads(companyId);
        /// //////////////////////////
        return ResponseEntity.ok(saved);
    }

   /* @GetMapping("/visible")
    public List<LeadmatrixEntity> getVisibleLeads(@RequestParam String email) {
        databaseCRM user = crmService.getUserByEmail(email);

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return leadmatrixRepository.findByCompanyId(user.getCompanyId());
        }
        if ("SALES".equalsIgnoreCase(user.getRole())) {
            return leadmatrixRepository.findByCompanyIdAndAssignedTo(user.getCompanyId(), user.getEmail());
        }
        return leadmatrixRepository.findByCompanyIdAndCreatedBy(user.getCompanyId(), user.getEmail());
    }*/
    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleLeads(@RequestParam String email, @RequestParam Long companyId) {
        databaseCRM user = crmService.getUserByEmail(email);


        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            return ResponseEntity.status(403).body("No company access");
        }
        if (!hasAccess(email, companyId)) {
            return forbidden();
        }


        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            return ResponseEntity.ok(leadmatrixRepository.findByCompanyId(companyId));
        }
        if (role == CompanyRole.SALES) {
            return ResponseEntity.ok(leadmatrixRepository.findByCompanyIdAndAssignedTo(companyId, email));
        }
        return ResponseEntity.ok(leadmatrixRepository.findByCompanyId(companyId));
    }

    // ================= GET ONE LEAD =================

    // Get All Leads

    @GetMapping("/all")
    public List<LeadmatrixEntity> getAllLeads() {
        return leadServices.getAllLeads();
    }

   /* @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable Long id) {
        LeadmatrixEntity lead = leadServices.getLeadById(id);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lead);
    }*/
   @GetMapping("/{id}")
   public ResponseEntity<?> getLeadById(@PathVariable Long id, @RequestParam String email) {
       databaseCRM user = crmService.getUserByEmail(email);
       LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);

       if (lead == null) {
           return ResponseEntity.notFound().build();
       }
       //if (!user.getCompanyId().equals(lead.getCompanyId())) {
         //  return ResponseEntity.status(403).body("Access denied");
       //}
       if (!companyAccessService.hasCompanyAccess(email, lead.getCompanyId())) {
           return ResponseEntity.status(403).body("Access denied");
       }

       if (!hasAccess(email, lead.getCompanyId())) {
           return forbidden();
       }


       return ResponseEntity.ok(lead);
   }

    @GetMapping("/search/{name}")
    public List<LeadmatrixEntity> searchLead(@PathVariable String name) {
        return leadmatrixRepository.findByName(name);
    }

    @GetMapping("/lead/my")
    public List<LeadmatrixEntity> myLeads(@RequestParam String email) {
        return leadmatrixRepository.findByAssignedTo(email);
    }

    @GetMapping("/lead/status")
    public List<LeadmatrixEntity> getLeadByStatus(@RequestParam String status) {
        return leadmatrixRepository.findByStatus(status);
    }

    /*// get hot leads api
    @GetMapping("/lead/hot")
    public List<LeadmatrixEntity> hotLeads(){
        return leadmatrixRepository.findByScoreGreaterThan(70);
    }*/

    // ================= HOT LEADS =================

    @GetMapping("/lead/hot")
    public ResponseEntity<?> hotLeads(@RequestParam String email,
                                      @RequestParam Long companyId) {

        if (!hasAccess(email, companyId)) {
            return forbidden();
        }

        return ResponseEntity.ok(
                leadmatrixRepository.findByCompanyIdAndScoreGreaterThan(companyId, 70)
        );
    }


    @PostMapping("/registerlead")
    public ResponseEntity<?> registerLead(@RequestBody LeadmatrixEntity lead) {
        if (lead.getStatus() == null) {
            lead.setStatus(LeadStatus.NEW);
        }
        if (lead.getCreatedDate() == null || lead.getCreatedDate().isBlank()) {
            lead.setCreatedDate(LocalDate.now().toString());
        }
        LeadmatrixEntity saved = leadServices.saveLead(lead);
        saveActivity(saved.getId(), "LEAD_CREATED", "Lead created: " + saved.getFullName());
        return ResponseEntity.ok(saved);
    }

    // Get Lead By Id
    @GetMapping("/lead/{id}")
    public LeadmatrixEntity getLead(@PathVariable Long id) {
        return leadServices.getLeadById(id);
    }


    // ================= DELETE LEAD =================

    /*// Delete Lead
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        leadServices.deleteLead(id);
        return ResponseEntity.ok("Lead deleted successfully");
    }*/
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id,  @RequestParam String email) {
        leadServices.deleteLead(id);
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);

        if (lead == null) {
            return ResponseEntity.notFound().build();
        }

        if (!hasAccess(email, lead.getCompanyId())) {
            return forbidden();
        }

        leadmatrixRepository.delete(lead);

        saveActivity(
                id,
                "LEAD_DELETED",
                "Lead deleted: " + lead.getFullName()
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lead deleted successfully"
        ));
        //return ResponseEntity.ok("Lead deleted successfully");
    }


    // ================= UPDATE LEAD =================


    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestParam String email, @RequestBody LeadmatrixEntity newLead) {

       // LeadmatrixEntity oldLead = leadServices.getLeadById(id);
        LeadmatrixEntity oldLead = leadmatrixRepository.findById(id).orElse(null);
        if (oldLead == null) {
            return ResponseEntity.notFound().build();
        }

        LeadmatrixEntity updated = leadServices.updateLead(id, newLead);
        saveActivity(id, "LEAD_UPDATED", "Lead updated: " + updated.getFullName());
       // return ResponseEntity.ok(updated);

/// ////////////////////////////////////////////
        if (!hasAccess(email, oldLead.getCompanyId())) {
            return forbidden();
        }

        oldLead.setOwnerName(newLead.getOwnerName());
        oldLead.setFirstName(newLead.getFirstName());
        oldLead.setLastName(newLead.getLastName());
        oldLead.setCompany(newLead.getCompany());
        oldLead.setTitle(newLead.getTitle());
        oldLead.setEmail(newLead.getEmail());
        oldLead.setOptIn(newLead.isOptIn());
        oldLead.setPhone(newLead.getPhone());
        oldLead.setFax(newLead.getFax());
        oldLead.setMobile(newLead.getMobile());
        oldLead.setWebsite(newLead.getWebsite());
        oldLead.setSource(newLead.getSource());
        oldLead.setStatus(newLead.getStatus());
        oldLead.setIndustry(newLead.getIndustry());
        oldLead.setEmployees(newLead.getEmployees());
        oldLead.setAnnualRevenue(newLead.getAnnualRevenue());
        oldLead.setRating(newLead.getRating());
        oldLead.setSkypeId(newLead.getSkypeId());
        oldLead.setSecondaryEmail(newLead.getSecondaryEmail());
        oldLead.setTwitter(newLead.getTwitter());
        oldLead.setFacebook(newLead.getFacebook());
        oldLead.setInstagram(newLead.getInstagram());
        oldLead.setLinkedin(newLead.getLinkedin());
        oldLead.setStreet(newLead.getStreet());
        oldLead.setCity(newLead.getCity());
        oldLead.setState(newLead.getState());
        oldLead.setZipCode(newLead.getZipCode());
        oldLead.setCountry(newLead.getCountry());
        oldLead.setDescription(newLead.getDescription());
        oldLead.setAssignedTo(newLead.getAssignedTo());
        oldLead.setScore(newLead.getScore());
        oldLead.setLastActivity(newLead.getLastActivity());

        LeadmatrixEntity saved = leadmatrixRepository.save(oldLead);

        saveActivity(
                id,
                "LEAD_UPDATED",
                "Lead updated: " + saved.getFullName()
        );

        return ResponseEntity.ok(saved);
    }

    // ================= STATUS UPDATE =================

    @PutMapping("/lead/status/{id}")
    public ResponseEntity<?> updateLeadStatus(@PathVariable Long id,
                                              @RequestParam String email,
                                              @RequestParam LeadStatus status) {

        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);

        if (lead == null) {
            return ResponseEntity.notFound().build();
        }

        if (!companyAccessService.hasCompanyAccess(email, lead.getCompanyId())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        LeadStatus oldStatus = lead.getStatus();
        lead.setStatus(status);

        LeadmatrixEntity saved = leadmatrixRepository.save(lead);

        saveActivity(
                id,
                "STATUS_CHANGED",
                "Status changed from " + oldStatus + " to " + status
        );

//
        if (status == LeadStatus.CUSTOMER) {
            notificationController.sendNotification(
                    "Lead converted to customer: " + saved.getFullName()
            );
        }

        return ResponseEntity.ok("Status updated successfully");
    }

    // ================= ASSIGN LEAD =================

    @PutMapping("/lead/assign/{id}")
    public ResponseEntity<?> assignLead(@PathVariable Long id,  @RequestParam String email, @RequestParam String salesEmail) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);

        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
/// /
        if (!hasAccess(email, lead.getCompanyId())) {
            return forbidden();
        }
/// /

        lead.setAssignedTo(salesEmail);
        LeadmatrixEntity saved = leadmatrixRepository.save(lead);
      //  leadmatrixRepository.save(lead);

        emailService.sendEmail(
                salesEmail,
                "New Lead Assigned",
                "You have been assigned lead: " + lead.getFullName()
        );

        notificationController.sendNotification("New lead assigned to " + salesEmail + ": " + saved.getFullName());
        saveActivity(id, "LEAD_ASSIGNED", "Lead assigned to: " + salesEmail);

        return ResponseEntity.ok("Lead assigned successfully" + (saved));
    }

//    @PostMapping("/lead/task")
//    public ResponseEntity<?> addTask(@RequestBody TaskEntity task) {
//        if (task.getTaskStatus() == null || task.getTaskStatus().isBlank()) {
//            task.setTaskStatus("PENDING");
//        }
//
//        TaskEntity saved = taskRepository.save(task);
//        saveActivity(saved.getLeadId(), "TASK_ADDED", "Task added: " + saved.getTaskTitle());
//        return ResponseEntity.ok(saved);
//    }
//
//    //@GetMapping("/lead/task/{leadId}")
//    //public List<TaskEntity> getTasksByLeadId(@PathVariable Long leadId) {
//     //   return taskRepository.findByLeadId(leadId);
//   // }
//    @GetMapping("/lead/task/{leadId}")
//    public ResponseEntity<?> getTasksByLeadId(@PathVariable Long leadId, @RequestParam String email) {
//        databaseCRM user = crmService.getUserByEmail(email);
//        LeadmatrixEntity lead = leadmatrixRepository.findById(leadId).orElse(null);
//
//        if (lead == null) {
//            return ResponseEntity.notFound().build();
//        }
//        /*if (!user.getCompanyId().equals(lead.getCompanyId())) {
//            return ResponseEntity.status(403).body("Access denied");
//        }*/
//        if (!companyAccessService.hasCompanyAccess(email, lead.getCompanyId())) {
//            return ResponseEntity.status(403).body("Access denied");
//        }
//        return ResponseEntity.ok(taskRepository.findByLeadId(leadId));
//    }
//
//
//    @PutMapping("/lead/task/status/{taskId}")
//    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
//        TaskEntity task = taskRepository.findById(taskId).orElse(null);
//        if (task == null) {
//            return ResponseEntity.notFound().build();
//        }
//        task.setTaskStatus(status);
//        taskRepository.save(task);
//        saveActivity(task.getLeadId(), "TASK_STATUS_UPDATED", "Task status changed to: " + status);
//        return ResponseEntity.ok("Task status updated");
//    }



    @PostMapping("/lead/activity")
    public ResponseEntity<?> addActivity(@RequestBody LeadActivity activity) {
        activity.setActivityDate(LocalDate.now().toString());
        LeadActivity saved = activityRepository.save(activity);
        return ResponseEntity.ok(saved);
    }


    @GetMapping("/lead/activity/{leadId}")
    public ResponseEntity<?> getLeadActivityByLeadId(@PathVariable Long leadId, @RequestParam String email) {
        databaseCRM user = crmService.getUserByEmail(email);
        LeadmatrixEntity lead = leadmatrixRepository.findById(leadId).orElse(null);

        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        if (!companyAccessService.hasCompanyAccess(email, lead.getCompanyId())) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok(activityRepository.findByLeadId(leadId));
    }


    @PostMapping("/lead/reminder")
    public ResponseEntity<?> addReminder(@RequestBody LeadReminder reminder) {
        LeadReminder saved = reminderRepository.save(reminder);
        saveActivity(saved.getLeadId(), "REMINDER_ADDED", "Reminder added for date " + saved.getReminderDate());
        return ResponseEntity.ok(saved);
    }


   @GetMapping("/lead/reminder/by-lead/{leadId}")
   public ResponseEntity<?> getReminderByLeadId(@PathVariable Long leadId, @RequestParam String email) {
       databaseCRM user = crmService.getUserByEmail(email);
       LeadmatrixEntity lead = leadmatrixRepository.findById(leadId).orElse(null);

       if (lead == null) {
           return ResponseEntity.notFound().build();
       }

       if (!companyAccessService.hasCompanyAccess(email, lead.getCompanyId())) {
           return ResponseEntity.status(403).body("Access denied");
       }

       return ResponseEntity.ok(reminderRepository.findByLeadId(leadId));
   }


    @GetMapping("/lead/reminder/by-date/{date}")
    public List<LeadReminder> getRemindersByDate(@PathVariable String date) {
        return reminderRepository.findByReminderDate(date);
    }

    @GetMapping("/lead/reminder/by-date")
    public List<LeadReminder> getVisibleRemindersByDate(@RequestParam String email, @RequestParam Long companyId,@RequestParam String date) {
        databaseCRM user = crmService.getUserByEmail(email);

        List<LeadmatrixEntity> visibleLeads  =  leadServices.getVisibleLeadsForUser(email,companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            visibleLeads = leadmatrixRepository.findByCompanyId(companyId);
        } else if (role == CompanyRole.SALES) {
            visibleLeads = leadmatrixRepository.findByCompanyIdAndAssignedTo(companyId, email);
        } else {
            visibleLeads = leadmatrixRepository.findByCompanyIdAndCreatedBy(companyId, email);
        }

       /* if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            visibleLeads = leadmatrixRepository.findByCompanyId(user.getCompanyId());
        } else if ("SALES".equalsIgnoreCase(user.getRole())) {
            visibleLeads = leadmatrixRepository.findByCompanyIdAndAssignedTo(user.getCompanyId(), user.getEmail());
        } else {
            visibleLeads = leadmatrixRepository.findByCompanyIdAndCreatedBy(user.getCompanyId(), user.getEmail());
        }*/

        List<LeadReminder> allReminders = reminderRepository.findByReminderDate(date);
        List<LeadReminder> filtered = new ArrayList<>();

        for (LeadReminder reminder : allReminders) {
            for (LeadmatrixEntity lead : visibleLeads) {
                if (lead.getId().equals(reminder.getLeadId())) {
                    filtered.add(reminder);
                    break;
                }
            }
        }

        return filtered;
    }


    @PostMapping("/lead/note")
    public ResponseEntity<?> addNote(@RequestBody LeadNote note) {
        note.setCreatedDate(LocalDate.now().toString());
        LeadNote saved = leadNoteRepository.save(note);
        saveActivity(saved.getLeadId(), "NOTE_ADDED", "Note added for lead");
        return ResponseEntity.ok(saved);
    }

    //@GetMapping("/lead/note/{leadId}")
    //public List<LeadNote> getNotesByLeadId(@PathVariable Long leadId) {
      //  return leadNoteRepository.findByLeadId(leadId);
    //}
    @GetMapping("/lead/note/{leadId}")
    public ResponseEntity<?> getNotesByLeadId(@PathVariable Long leadId, @RequestParam String email) {
        databaseCRM user = crmService.getUserByEmail(email);
        LeadmatrixEntity lead = leadmatrixRepository.findById(leadId).orElse(null);

        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        if (!companyAccessService.hasCompanyAccess(email, lead.getCompanyId())) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok(leadNoteRepository.findByLeadId(leadId));
    }



    @PostMapping("/lead/import")
    public ResponseEntity<?> importLeads(@RequestParam("file") MultipartFile file) {
        try {
            excelLeadService.importLeads(file);
            return ResponseEntity.ok("Leads imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing file: " + e.getMessage());
        }
    }


    @GetMapping("/lead/document/{filename}")
    public ResponseEntity<?> getDocument(@PathVariable String filename) {
        try {
            Path path = Paths.get("uploads").resolve(filename);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("File read failed");
        }
    }


    @GetMapping("/dashboard/total-leads")
    public long totalLeads(){
        return leadmatrixRepository.count();
    }

    @GetMapping("/dashboard/status")
    public long leadByStatus(@RequestParam String status) {
        return leadmatrixRepository.countByStatus(status);
    }

    @GetMapping("/dashboard/customers")
    public long totalCustomer(){
        return leadmatrixRepository.countByStatus("Customer");
    }

    @GetMapping("/dashboard/lost")
    public long totalLost(){
        return leadmatrixRepository.countByStatus("Lost");
    }

    @GetMapping("/dashboard/contacted")
    public long totalContacted() {
        return leadmatrixRepository.countByStatus("CONTACTED");
    }

    @GetMapping("/dashboard/qualified")
    public long totalQualified() {
        return leadmatrixRepository.countByStatus("QUALIFIED");
    }





    @GetMapping("/dashboard/sales-performance")
    public long salesPerformance(@RequestParam String email,
                                 @RequestParam Long companyId) {

        return leadmatrixRepository.findByCompanyIdAndAssignedTo(companyId, email)
                .stream()
                .filter(l -> l.getStatus() == LeadStatus.CUSTOMER)
                .count();
    }

    @GetMapping("/dashboard/conversion-rate")
    public double conversionRate() {
        long total = leadmatrixRepository.count();
        long customers = leadmatrixRepository.countByStatus("CUSTOMER");
        return total == 0 ? 0 : (customers * 100.0) / total;
    }

    // ================= SOURCE SUMMARY =================

    @GetMapping("/dashboard/source-summary")
    public ResponseEntity<?> sourceSummary(@RequestParam String email,
                                           @RequestParam Long companyId) {

        if (!hasAccess(email, companyId)) {
            return forbidden();
        }

        return ResponseEntity.ok(Map.of(
                "facebook", leadmatrixRepository.countByCompanyIdAndSourceIgnoreCase(companyId, "Facebook"),
                "website", leadmatrixRepository.countByCompanyIdAndSourceIgnoreCase(companyId, "Website"),
                "referral", leadmatrixRepository.countByCompanyIdAndSourceIgnoreCase(companyId, "Referral")
        ));
    }
//

    // ================= DASHBOARD CONVERSION RATE =================

    @GetMapping("/report/conversion-rate")
    public ResponseEntity<?> conversionRateReport(@RequestParam String email,
                                                  @RequestParam Long companyId) {

        if (!hasAccess(email, companyId)) {
            return forbidden();
        }

        long total = leadmatrixRepository.countByCompanyId(companyId);
        long customers = leadmatrixRepository.countByCompanyIdAndStatusIgnoreCase(companyId, "CUSTOMER");

        double rate = total == 0 ? 0 : (customers * 100.0) / total;

        return ResponseEntity.ok(rate);
    }

    @GetMapping("/report/source")
    public long sourceReport(@RequestParam String source) {
        return leadmatrixRepository.countBySource(source);
    }



    @GetMapping("/report/sales")
    public long salesReport(@RequestParam String email) {
        return leadmatrixRepository.countByAssignedTo(email);
    }

    @GetMapping("/report/date")
    public long dateReport(@RequestParam String date) {
        return leadmatrixRepository.countByCreatedDate(date);
    }

    @GetMapping("/report/top-sales")
    public ResponseEntity<?> topSales() {
        List<databaseCRM> users = crmRespository.findAll();
        String topEmail = "";
        long max = 0;

        for (databaseCRM user : users) {
            long count = leadmatrixRepository.countByAssignedTo(user.getEmail());
            if (count > max) {
                max = count;
                topEmail = user.getEmail();
            }
        }

        return ResponseEntity.ok(java.util.Map.of("email", topEmail, "count", max));
    }

    @GetMapping("/report/team-performance")
    public ResponseEntity<?> teamPerformance(@RequestParam String email,
                                             @RequestParam Long companyId) {

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        List<databaseCRM> users = crmRespository.findAll();
        List<java.util.Map<String, Object>> report = new java.util.ArrayList<>();

        for (databaseCRM user : users) {
            if ("USER".equalsIgnoreCase(user.getRole()) || "SALES".equalsIgnoreCase(user.getRole())) {

                long assigned = leadmatrixRepository
                        .findByCompanyIdAndAssignedTo(companyId, user.getEmail())
                        .size();

                long customers = leadmatrixRepository
                        .findByCompanyIdAndAssignedTo(companyId, user.getEmail())
                        .stream()
                        .filter(l -> l.getStatus() == LeadStatus.CUSTOMER)
                        .count();

                java.util.Map<String, Object> row = new java.util.HashMap<>();
                row.put("name", user.getName());
                row.put("email", user.getEmail());
                row.put("assigned", assigned);
                row.put("customers", customers);

                report.add(row);
            }
        }

        return ResponseEntity.ok(report);
    }



    @GetMapping("/company/leads/{companyId}")
    public List<LeadmatrixEntity> companyLeads(@PathVariable Long companyId) {
        return leadmatrixRepository.findByCompanyId(companyId);
    }

    @GetMapping("/company/plan/{companyId}")
    public ResponseEntity<?> getCompanyPlan(@PathVariable Long companyId) {
        Optional<Subscription> sub = subscriptionRepository.findByCompanyId(companyId);
        return ResponseEntity.ok(sub.orElse(null));
    }



    @GetMapping("/page")
    public ResponseEntity<?> getLeadsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size);

        return ResponseEntity.ok(leadmatrixRepository.findAll(pageable));
    }




    @GetMapping("/filter")
    public ResponseEntity<?> filterLeads(@RequestParam String email,
                                         @RequestParam Long companyId,
                                         @RequestParam(required = false) LeadStatus status,
                                         @RequestParam(required = false) LeadSource source,
                                         @RequestParam(required = false) String assignedTo) {

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        List<LeadmatrixEntity> leads = leadmatrixRepository.findByCompanyId(companyId);

        List<LeadmatrixEntity> filtered = leads.stream()
                .filter(l -> status == null || l.getStatus() == status)
                .filter(l -> source == null || l.getSource() == source)
                .filter(l -> assignedTo == null || assignedTo.isBlank()
                        || assignedTo.equalsIgnoreCase(l.getAssignedTo()))
                .toList();

        return ResponseEntity.ok(filtered);
    }

    @PostMapping("/lead/upload/{id}")
    public ResponseEntity<?> uploadDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            LeadmatrixEntity lead = leadServices.getLeadById(id);
            if (lead == null) {
                return ResponseEntity.notFound().build();
            }
            String fileName = file.getOriginalFilename();
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");

            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }
            java.nio.file.Files.copy(
                    file.getInputStream(),
                    uploadPath.resolve(fileName),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            lead.setDocument(fileName);
            leadmatrixRepository.save(lead);

            saveActivity(
                    id,
                    "DOCUMENT_UPLOADED",
                    "Document uploaded: " + fileName
            );
            return ResponseEntity.ok("Document uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }



    // OWNER / ADMIN / MANAGER can view all company leads
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/report/all")
    public ResponseEntity<?> allLeads(@RequestParam String email,
                                      @RequestParam Long companyId) {

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(leadmatrixRepository.findByCompanyId(companyId));
    }


    // Sales/User can view only assigned leads
    @PreAuthorize("hasAnyRole('USER','SALES')")
    @GetMapping("/sales/leads")
    public ResponseEntity<?> salesLeads(@RequestParam String email,
                                        @RequestParam Long companyId) {

        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(
                leadmatrixRepository.findByCompanyIdAndAssignedTo(companyId, email)
        );
    }
    }
