package com.leadmatrix.crm.controller;


import com.leadmatrix.crm.entity.*;
import com.leadmatrix.crm.respository.*;
import com.leadmatrix.crm.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private crmRespository crmRespository;

    @Autowired
    private LeadmatrixRespository leadmatrixRepository;

    @Autowired
    private LeadNoteRepository leadNoteRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private LeadTaskRepository leadTaskRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ExcelLeadService excelLeadService;

    @Autowired
    private TwilioService twilioService;


    // updatelead status()
    private void saveActivity(Long leadId, String type, String description) {
        LeadActivity activity = new LeadActivity();
        activity.setLeadId(leadId);
        activity.setActivityType(type);
        activity.setDescription(description);
        activity.setActivityDate(java.time.LocalDate.now().toString());
        activityRepository.save(activity);
// want to create lead()
    }

    // Add Lead  ///////////////////////////////////
    /*@PostMapping("/add")
    public ResponseEntity<?> createLead(@RequestBody LeadmatrixEntity lead) {
        if (lead.getStatus() == null || lead.getStatus().isEmpty()) {
            lead.setStatus("NEW");
        }
        LeadmatrixEntity saved = leadServices.saveLead(lead);
        saveActivity(
                saved.getId(),
                "LEAD_CREATED",
                "Lead created: " + saved.getName()
        );
        if (saved.getAssignedTo() != null && !saved.getAssignedTo().isEmpty()) {
            emailService.sendEmail(
                    saved.getAssignedTo(),
                    "New Lead Assigned",
                    "A new lead has been assigned to you: " + saved.getName()
            );
            saveActivity(
                    saved.getId(),
                    "LEAD_ASSIGNED",
                    "Lead assigned to: " + saved.getAssignedTo()
            );
        }
            if (lead.getName() == null || lead.getName().isBlank()) {
                return ResponseEntity.badRequest().body("Name is required");
            }
        if (lead.getEmail() != null && !lead.getEmail().isBlank() && !lead.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("Invalid email");
        }
            if (lead.getPhone() == null || lead.getPhone().isBlank()) {
                return ResponseEntity.badRequest().body("Phone is required");
            }
            if (lead.getStatus() == null || lead.getStatus().isEmpty()) {
                lead.setStatus("NEW");
            }
            if (lead.getSource() == null || lead.getSource().isBlank()) {
                lead.setSource("Unknown");
            }

            saveActivity(saved.getId(), "LEAD_CREATED", "Lead created: " + saved.getName());
            return ResponseEntity.ok(saved);
        }*/
   // @PostMapping("/add")
    //public ResponseEntity<?> createLead(@RequestBody LeadmatrixEntity lead) {
    @PostMapping("/add")
    public ResponseEntity<?> createLead(@RequestParam String email, @RequestBody LeadmatrixEntity lead) {
        databaseCRM user = crmService.getUserByEmail(email);

        if (lead.getName() == null || lead.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (lead.getPhone() == null || lead.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body("Phone is required");
        }

        if (lead.getEmail() != null && !lead.getEmail().isBlank() && !lead.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("Invalid email");
        }

        if (lead.getStatus() == null || lead.getStatus().isBlank()) {
            lead.setStatus("NEW");
        }

        if (lead.getSource() == null || lead.getSource().isBlank()) {
            lead.setSource("Unknown");
        }

        if (lead.getCreatedDate() == null || lead.getCreatedDate().isBlank()) {
            lead.setCreatedDate(LocalDate.now().toString());
        }

        lead.setCompanyId(user.getCompanyId());
        LeadmatrixEntity saved = leadServices.saveLead(lead);

        saveActivity(saved.getId(), "LEAD_CREATED", "Lead created: " + saved.getName());

        if (saved.getAssignedTo() != null && !saved.getAssignedTo().isBlank()) {
            emailService.sendEmail(
                    saved.getAssignedTo(),
                    "New Lead Assigned",
                    "A new lead has been assigned to you: " + saved.getName()
            );
            saveActivity(saved.getId(), "LEAD_ASSIGNED", "Lead assigned to: " + saved.getAssignedTo());
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/visible")
    public List<LeadmatrixEntity> getVisibleLeads(@RequestParam String email) {
        databaseCRM user = crmService.getUserByEmail(email);

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return leadmatrixRepository.findByCompanyId(user.getCompanyId());
        }
        if ("SALES".equalsIgnoreCase(user.getRole())) {
            return leadmatrixRepository.findByCompanyIdAndAssignedTo(user.getCompanyId(), user.getEmail());
        }
        return leadmatrixRepository.findByCompanyIdAndCreatedBy(user.getCompanyId(), user.getEmail());
    }

    // Get All Leads
    /*@GetMapping("/{id}")
    public LeadmatrixEntity getLeadById(@PathVariable Long id) {
        return leadmatrixRepository.findById(id).orElse(null);
    }*/
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
       if (!user.getCompanyId().equals(lead.getCompanyId())) {
           return ResponseEntity.status(403).body("Access denied");
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

    @GetMapping("/lead/hot")
    public List<LeadmatrixEntity> hotLeads() {
        return leadmatrixRepository.findByScoreGreaterThan(70);
    }

    @PostMapping("/registerlead")
    public ResponseEntity<?> registerLead(@RequestBody LeadmatrixEntity lead) {
        if (lead.getStatus() == null || lead.getStatus().isBlank()) {
            lead.setStatus("NEW");
        }
        if (lead.getCreatedDate() == null || lead.getCreatedDate().isBlank()) {
            lead.setCreatedDate(LocalDate.now().toString());
        }
        LeadmatrixEntity saved = leadServices.saveLead(lead);
        saveActivity(saved.getId(), "LEAD_CREATED", "Lead created: " + saved.getName());
        return ResponseEntity.ok(saved);
    }

    // Get Lead By Id
    @GetMapping("/lead/{id}")
    public LeadmatrixEntity getLead(@PathVariable Long id) {
        return leadServices.getLeadById(id);
    }

    /*// Delete Lead
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        leadServices.deleteLead(id);
        return ResponseEntity.ok("Lead deleted successfully");
    }*/
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        leadServices.deleteLead(id);
        return ResponseEntity.ok("Lead deleted successfully");
    }

    // Update Lead ////////////////////////////////////////////////////////////////////
    /*@PutMapping("/update/{id}")
    public LeadmatrixEntity updateLead(@PathVariable Long id, @RequestBody LeadmatrixEntity lead) {
        return LeadServices.updateLead(id, lead);
    }*/
    /*@PutMapping("/update/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody LeadmatrixEntity newLead) {
        LeadmatrixEntity updated = leadServices.updateLead(id, newLead);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody LeadmatrixEntity newLead) {
        LeadmatrixEntity oldLead = leadServices.getLeadById(id);
        if (oldLead == null) {
            return ResponseEntity.notFound().build();
        }
        LeadmatrixEntity updated = leadServices.updateLead(id, newLead);
        saveActivity(
                id,
                "LEAD_UPDATED",
                "Lead updated: " + updated.getName()
        );
        return ResponseEntity.ok(updated);
    }*/
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody LeadmatrixEntity newLead) {
        LeadmatrixEntity oldLead = leadServices.getLeadById(id);
        if (oldLead == null) {
            return ResponseEntity.notFound().build();
        }

        LeadmatrixEntity updated = leadServices.updateLead(id, newLead);
        saveActivity(id, "LEAD_UPDATED", "Lead updated: " + updated.getName());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/lead/status/{id}")
    public ResponseEntity<?> updateLeadStatus(@PathVariable Long id, @RequestParam String status) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }

        lead.setStatus(status);
        leadmatrixRepository.save(lead);
        saveActivity(id, "STATUS_CHANGED", "Lead status changed to: " + status);

        if ("CUSTOMER".equalsIgnoreCase(status)) {
            emailService.sendEmail(
                    "admin@gmail.com",
                    "Lead Converted",
                    "Lead converted to customer: " + lead.getName()
            );
        }

        return ResponseEntity.ok("Status updated successfully");
    }
   /* @PutMapping("/lead/status/{id}")
    public ResponseEntity<?> updateLeadStatus(@PathVariable Long id, @RequestParam String status) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElseThrow();

        String oldstatus = lead.getStatus();
        lead.setStatus(status);
        leadmatrixRepository.save(lead);

        saveActivity(
                lead.getId(),
                "STATUS_CHANGED",
                "Status changed from" + oldstatus + "to" + status
        );
        if ("CUSTOMER".equalsIgnoreCase(status) && lead.getEmail() != null && !lead.getEmail().isEmpty()) {
            notificationController.sendNotification(
                    "Lead converted to customer: " + lead.getName()
            );
            emailService.sendEmail(
                    lead.getEmail(),
                    "Congratulations",
                    "Thank you for becoming our customer."
            );
        }
        return ResponseEntity.ok("Lead status updated successfully");
    }*/

    @PutMapping("/lead/assign/{id}")
    public ResponseEntity<?> assignLead(@PathVariable Long id, @RequestParam String salesEmail) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }

        lead.setAssignedTo(salesEmail);
        leadmatrixRepository.save(lead);

        emailService.sendEmail(
                salesEmail,
                "New Lead Assigned",
                "You have been assigned lead: " + lead.getName()
        );

        notificationController.sendNotification("New lead assigned to " + salesEmail);
        saveActivity(id, "LEAD_ASSIGNED", "Lead assigned to: " + salesEmail);

        return ResponseEntity.ok("Lead assigned successfully");
    }
    /*@PutMapping("/lead/assign/{id}")
    public ResponseEntity<?> assignLead(@PathVariable Long id, @RequestParam String salesEmail) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElseThrow();
        lead.setAssignedTo(salesEmail);
        leadmatrixRepository.save(lead);
        // 🔥 ADD THIS
        notificationController.sendNotification(
                "Lead assigned to " + salesEmail + ": " + lead.getName()
        );
        saveActivity(
                lead.getId(),
                "LEAD_ASSIGNED",
                "Lead assigned to" + salesEmail
        );
        if (salesEmail != null && !salesEmail.isEmpty()) {
            emailService.sendEmail(
                    salesEmail,
                    "New Lead Assigned",
                    "Lead assigned to you: " + lead.getName()
            );
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Lead assigned successfully", lead));
    }*/


   /* @PostMapping("/lead/task")
    public ResponseEntity<?> addTask(@RequestBody LeadTask task) {
        if (task.getTaskStatus() == null || task.getTaskStatus().isEmpty()) {
            task.setTaskStatus("PENDING");
        }
        LeadTask saved = leadTaskRepository.save(task);

        saveActivity(
                task.getLeadId(),
                "TASK_ADDED",
                "Task added: " + task.getTaskTitle()
        );
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/lead/task/{leadId}")
    public List<LeadTask> getTasksByLeadId(@PathVariable Long leadId) {
        return leadTaskRepository.findByLeadId(leadId);
    }

    @PutMapping("/lead/task/status/{taskId}")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        LeadTask task = leadTaskRepository.findById(taskId).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        task.setTaskStatus(status);
        leadTaskRepository.save(task);

        return ResponseEntity.ok("Task status updated");
    }*/
    @PostMapping("/lead/task")
    public ResponseEntity<?> addTask(@RequestBody LeadTask task) {
        if (task.getTaskStatus() == null || task.getTaskStatus().isBlank()) {
            task.setTaskStatus("PENDING");
        }

        LeadTask saved = leadTaskRepository.save(task);
        saveActivity(saved.getLeadId(), "TASK_ADDED", "Task added: " + saved.getTaskTitle());
        return ResponseEntity.ok(saved);
    }

    //@GetMapping("/lead/task/{leadId}")
    //public List<LeadTask> getTasksByLeadId(@PathVariable Long leadId) {
     //   return leadTaskRepository.findByLeadId(leadId);
   // }
    @GetMapping("/lead/task/{leadId}")
    public ResponseEntity<?> getTasksByLeadId(@PathVariable Long leadId, @RequestParam String email) {
        databaseCRM user = crmService.getUserByEmail(email);
        LeadmatrixEntity lead = leadmatrixRepository.findById(leadId).orElse(null);

        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        if (!user.getCompanyId().equals(lead.getCompanyId())) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok(leadTaskRepository.findByLeadId(leadId));
    }


    @PutMapping("/lead/task/status/{taskId}")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        LeadTask task = leadTaskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        task.setTaskStatus(status);
        leadTaskRepository.save(task);
        saveActivity(task.getLeadId(), "TASK_STATUS_UPDATED", "Task status changed to: " + status);
        return ResponseEntity.ok("Task status updated");
    }

    /*@PostMapping("/lead/activity")///  ////////////////////
    public ResponseEntity<?> addActivity(@RequestBody LeadActivity activity) {
        activity.setActivityDate(LocalDate.now().toString());
        return ResponseEntity.ok(activityRepository.save(activity));
    }

    @GetMapping("/lead/activity/{leadId}")
    public List<LeadActivity> getLeadActivityByLeadId(@PathVariable Long leadId){

        return activityRepository.findByLeadId(leadId);

    }*/

    @PostMapping("/lead/activity")
    public ResponseEntity<?> addActivity(@RequestBody LeadActivity activity) {
        activity.setActivityDate(LocalDate.now().toString());
        LeadActivity saved = activityRepository.save(activity);
        return ResponseEntity.ok(saved);
    }

    //@GetMapping("/lead/activity/{leadId}")
    //public List<LeadActivity> getLeadActivityByLeadId(@PathVariable Long leadId) {
      //  return activityRepository.findByLeadId(leadId);
    //}
    @GetMapping("/lead/activity/{leadId}")
    public ResponseEntity<?> getLeadActivityByLeadId(@PathVariable Long leadId, @RequestParam String email) {
        databaseCRM user = crmService.getUserByEmail(email);
        LeadmatrixEntity lead = leadmatrixRepository.findById(leadId).orElse(null);

        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        if (!user.getCompanyId().equals(lead.getCompanyId())) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok(activityRepository.findByLeadId(leadId));
    }

   /* @PostMapping("/lead/reminder")/// //////////////////////////////
    public ResponseEntity<?> addReminder(@RequestBody LeadReminder reminder) {
        LeadReminder saved = reminderRepository.save(reminder);

        saveActivity(
                saved.getLeadId(),
                "REMINDER_ADDED",
                "Reminder added for date " + saved.getReminderDate()
        );
        return ResponseEntity.ok(reminderRepository.save(reminder));
    }

    @GetMapping("/lead/reminder/{leadId}")
    public List<LeadReminder> getReminderByLeadId(@PathVariable Long leadId) {
        return reminderRepository.findByLeadId(leadId);
    }

    @GetMapping("/lead/reminder/{date}")
    public List<LeadReminder> getRemindersByDate(@PathVariable String date){
        return reminderRepository.findByReminderDate(date);
    }*/

    @PostMapping("/lead/reminder")
    public ResponseEntity<?> addReminder(@RequestBody LeadReminder reminder) {
        LeadReminder saved = reminderRepository.save(reminder);
        saveActivity(saved.getLeadId(), "REMINDER_ADDED", "Reminder added for date " + saved.getReminderDate());
        return ResponseEntity.ok(saved);
    }

   // @GetMapping("/lead/reminder/by-lead/{leadId}")
    //public List<LeadReminder> getReminderByLeadId(@PathVariable Long leadId) {
      //  return reminderRepository.findByLeadId(leadId);
    //}
   @GetMapping("/lead/reminder/by-lead/{leadId}")
   public ResponseEntity<?> getReminderByLeadId(@PathVariable Long leadId, @RequestParam String email) {
       databaseCRM user = crmService.getUserByEmail(email);
       LeadmatrixEntity lead = leadmatrixRepository.findById(leadId).orElse(null);

       if (lead == null) {
           return ResponseEntity.notFound().build();
       }

       if (!user.getCompanyId().equals(lead.getCompanyId())) {
           return ResponseEntity.status(403).body("Access denied");
       }

       return ResponseEntity.ok(reminderRepository.findByLeadId(leadId));
   }


    @GetMapping("/lead/reminder/by-date/{date}")
    public List<LeadReminder> getRemindersByDate(@PathVariable String date) {
        return reminderRepository.findByReminderDate(date);
    }

    @GetMapping("/lead/reminder/by-date")
    public List<LeadReminder> getVisibleRemindersByDate(@RequestParam String email, @RequestParam String date) {
        databaseCRM user = crmService.getUserByEmail(email);

        List<LeadmatrixEntity> visibleLeads  =  leadServices.getVisibleLeadsForUser(email);


        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            visibleLeads = leadmatrixRepository.findByCompanyId(user.getCompanyId());
        } else if ("SALES".equalsIgnoreCase(user.getRole())) {
            visibleLeads = leadmatrixRepository.findByCompanyIdAndAssignedTo(user.getCompanyId(), user.getEmail());
        } else {
            visibleLeads = leadmatrixRepository.findByCompanyIdAndCreatedBy(user.getCompanyId(), user.getEmail());
        }

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

   /* @PostMapping("/lead/note")/// //////////////////////////////////
    public ResponseEntity<?> addNote(@RequestBody LeadNote note) {
        note.setCreatedDate(java.time.LocalDate.now().toString());
        LeadNote saved = leadNoteRepository.save(note);

        saveActivity(
                saved.getLeadId(),
                "NOTE_ADDED",
                "Note added for lead"
        );
        return ResponseEntity.ok(leadNoteRepository.save(note));
    }

    @GetMapping("/lead/note/{leadId}")
    public List<LeadNote> getNotesByLeadId(@PathVariable Long leadId){

        return leadNoteRepository.findByLeadId(leadId);
    }*/


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
        if (!user.getCompanyId().equals(lead.getCompanyId())) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok(leadNoteRepository.findByLeadId(leadId));
    }

    /*@PostMapping("/lead/import")
    public String importLeads(
            @RequestParam("file") MultipartFile file) {

        try {
            excelLeadService.importLeads(file);
            return "Leads imported successfully";
        } catch (Exception e) {
            return "Error importing file";
        }
    }*/

    @PostMapping("/lead/import")
    public ResponseEntity<?> importLeads(@RequestParam("file") MultipartFile file) {
        try {
            excelLeadService.importLeads(file);
            return ResponseEntity.ok("Leads imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing file: " + e.getMessage());
        }
    }

    // upar wale coad ko commet kardo
    /*@GetMapping("/lead/document/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws Exception {

        Path path = Paths.get("uploads/" + filename);

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok().body(resource);
    }*/
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


     /*@GetMapping("/lead/my")
    public List<LeadmatrixEntity> myLead(@RequestParam String email) {
        return leadmatrixRepository.findByAssignedTo(email);
    }*/

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



    /*@GetMapping("/dashboard/sales-performance")
    public long salesPerformance(@RequestParam String email){
        return leadmatrixRepository.countByAssignedTo(email);
    }
    @GetMapping("/dashboard/sales-performance")
    public long salesPerformance(@RequestParam String email) {
        return leadmatrixRepository.findByAssignedTo(email)
                .stream()
                .filter(l -> "CUSTOMER".equalsIgnoreCase(l.getStatus()))
                .count();
    }

    @GetMapping("/dashboard/conversion-rate")
    public double conversionRate() {
        long total = leadmatrixRepository.count();
        long customers = leadmatrixRepository.countByStatus("CUSTOMER");
        if (total == 0) {
            return 0;
        }
        return (customers * 100.0) / total;
    }

    @GetMapping("/dashboard/source-summary")
    public ResponseEntity<?> sourceSummary() {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "facebook", leadmatrixRepository.countBySource("Facebook"),
                        "website", leadmatrixRepository.countBySource("Website"),
                        "referral", leadmatrixRepository.countBySource("Referral")
                )
        );
    }*/


    @GetMapping("/dashboard/sales-performance")
    public long salesPerformance(@RequestParam String email) {
        return leadmatrixRepository.findByAssignedTo(email)
                .stream()
                .filter(l -> "CUSTOMER".equalsIgnoreCase(l.getStatus()))
                .count();
    }

    @GetMapping("/dashboard/conversion-rate")
    public double conversionRate() {
        long total = leadmatrixRepository.count();
        long customers = leadmatrixRepository.countByStatus("CUSTOMER");
        return total == 0 ? 0 : (customers * 100.0) / total;
    }

    @GetMapping("/dashboard/source-summary")
    public ResponseEntity<?> sourceSummary() {
        return ResponseEntity.ok(java.util.Map.of(
                "facebook", leadmatrixRepository.countBySource("Facebook"),
                "website", leadmatrixRepository.countBySource("Website"),
                "referral", leadmatrixRepository.countBySource("Referral")
        ));
    }

   /* @GetMapping("/report/conversion-rate")
    public double conversionRateReport() {
        long total = leadmatrixRepository.count();
        long customers = leadmatrixRepository.countByStatus("CUSTOMER");
        if (total == 0) return 0;
        return (customers * 100.0) / total;
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
        return ResponseEntity.ok(java.util.Map.of(
                "email", topEmail,
                "count", max
        ));
    }
    @GetMapping("/report/team-performance")
    public ResponseEntity<?> teamPerformance() {
        List<databaseCRM> users = crmRespository.findAll();
        List<java.util.Map<String, Object>> report = new java.util.ArrayList<>();
        for (databaseCRM user : users) {
            if ("USER".equalsIgnoreCase(user.getRole()) || "SALES".equalsIgnoreCase(user.getRole())) {
                long assigned = leadmatrixRepository.countByAssignedTo(user.getEmail());
                long customers = leadmatrixRepository.findByAssignedTo(user.getEmail())
                        .stream()
                        .filter(l -> "CUSTOMER".equalsIgnoreCase(l.getStatus()))
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
    }  */

    @GetMapping("/report/conversion-rate")
    public double conversionRateReport() {
        long total = leadmatrixRepository.count();
        long customers = leadmatrixRepository.countByStatus("CUSTOMER");
        return total == 0 ? 0 : (customers * 100.0) / total;
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
    public ResponseEntity<?> teamPerformance() {
        List<databaseCRM> users = crmRespository.findAll();
        List<java.util.Map<String, Object>> report = new java.util.ArrayList<>();

        for (databaseCRM user : users) {
            if ("USER".equalsIgnoreCase(user.getRole()) || "SALES".equalsIgnoreCase(user.getRole())) {
                long assigned = leadmatrixRepository.countByAssignedTo(user.getEmail());
                long customers = leadmatrixRepository.findByAssignedTo(user.getEmail())
                        .stream()
                        .filter(l -> "CUSTOMER".equalsIgnoreCase(l.getStatus()))
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

    /*// get lead by company
    @GetMapping("/company/leads/{companyId}")
    public List<LeadmatrixEntity> companyLeads(@PathVariable Long companyId) {

        return leadmatrixRepository.findByCompanyId(companyId);
    }
    @GetMapping("/company/plan/{companyId}")
    public ResponseEntity<?> getCompanyPlan(@PathVariable Long companyId) {
        Optional<Subscription> sub = subscriptionRepository.findByCompanyId(companyId);
        return ResponseEntity.ok(sub.orElse(null));
    }*/


    @GetMapping("/company/leads/{companyId}")
    public List<LeadmatrixEntity> companyLeads(@PathVariable Long companyId) {
        return leadmatrixRepository.findByCompanyId(companyId);
    }

    @GetMapping("/company/plan/{companyId}")
    public ResponseEntity<?> getCompanyPlan(@PathVariable Long companyId) {
        Optional<Subscription> sub = subscriptionRepository.findByCompanyId(companyId);
        return ResponseEntity.ok(sub.orElse(null));
    }

   /* @PostMapping("/send-whatsapp")
    public String sendMessage(@RequestParam String phone) {
        twilioService.sendWhatsAppMessage(
                phone,
                "Hello from CRM 🚀"
        );
        return "Message Sent";
    }*/

    @PostMapping("/send-whatsapp")
    public String sendMessage(@RequestParam String phone) {
        twilioService.sendWhatsAppMessage(phone, "Hello from CRM 🚀");
        return "Message Sent";
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
    public List<LeadmatrixEntity> filterLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String assignedTo
    ) {
        List<LeadmatrixEntity> leads = leadmatrixRepository.findAll();

        return leads.stream()
                .filter(l -> status == null || status.isBlank() || status.equalsIgnoreCase(l.getStatus()))
                .filter(l -> source == null || source.isBlank() || source.equalsIgnoreCase(l.getSource()))
                .filter(l -> assignedTo == null || assignedTo.isBlank() || assignedTo.equalsIgnoreCase(l.getAssignedTo()))
                .toList();
    }

    /*@PutMapping("/lead/status/{id}")
    public LeadmatrixEntity updateLeadStatus(@PathVariable Long id,
                                             @RequestParam String status) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);
        if (lead == null) return null;
        lead.setStatus(status);
        if (status.equalsIgnoreCase("CUSTOMER")) {
            emailService.sendEmail(
                    "admin@gmail.com",
                    "Lead Converted",
                    "Lead " + lead.getName() + " has been converted to CUSTOMER"
            );
        }
        return leadmatrixRepository.save(lead);
    }*/




    /*@PostMapping("/lead/upload/{id}")
    public String uploadFile(@PathVariable Long id,
                             @RequestParam("file") MultipartFile file) {
        try {
            LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);
            if (lead == null) {
                return "Lead not found";
            }
            String fileName = file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.write(path, file.getBytes());
            lead.setDocument(fileName);
            leadmatrixRepository.save(lead);
            return "File uploaded";

        } catch (Exception e) {
            return "Upload failed";
        }
    }*/
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
   /* @PutMapping("/lead/assign/{id}")
    public LeadmatrixEntity assignLead(@PathVariable Long id,
                                       @RequestParam String salesEmail) {
          LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);
        if (lead != null) {
            lead.setAssignedTo(salesEmail);
            emailService.sendEmail(
                    salesEmail,
                    "New Lead Assigned",
                    "You have been assigned lead :"+lead.getName()
            );
            return leadmatrixRepository.save(lead);
        }
        return null;
    }*/

    // Register Lead ////////////////////////////////////////////////////////////
    //@PostMapping("/registerlead")
    //public LeadmatrixEntity registerLead(@RequestBody LeadmatrixEntity lead) {
       // return leadServices.saveLead(lead);
    }