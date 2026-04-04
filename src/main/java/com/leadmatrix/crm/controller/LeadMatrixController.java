package com.leadmatrix.crm.controller;


import com.leadmatrix.crm.dpo.ApiResponse;
import com.leadmatrix.crm.entity.*;
import com.leadmatrix.crm.respository.*;
import com.leadmatrix.crm.services.EmailService;
import com.leadmatrix.crm.services.ExcelLeadService;
import com.leadmatrix.crm.services.TwilioService;
import com.leadmatrix.crm.services.leadServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class LeadMatrixController {


    @Autowired
    private leadServices leadServices;

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
    private EmailService emailService;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private SubscriptionRepository subscriptionRepository;



    // Add Lead  ///////////////////////////////////
    @PostMapping("/add")
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
        }



    // Get All Leads
    @GetMapping("/all")
    public List<LeadmatrixEntity> getAllLeads() {
        return leadServices.getAllLeads();
    }

    /*@GetMapping("/{id}")
    public LeadmatrixEntity getLeadById(@PathVariable Long id) {
        return leadmatrixRepository.findById(id).orElse(null);
    }*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable Long id) {
        LeadmatrixEntity lead = leadServices.getLeadById(id);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lead);
    }


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



    @GetMapping("/search/{name}")
    public List<LeadmatrixEntity> searchLead(@PathVariable String name) {

        return leadmatrixRepository.findByName(name);
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
    @PutMapping("/lead/status/{id}")
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
    }



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


    @GetMapping("/lead/document/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws Exception {

        Path path = Paths.get("uploads/" + filename);

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/lead/status")
    public List<LeadmatrixEntity> getLeadByStatus(@RequestParam String status) {

        return leadmatrixRepository.findByStatus(status);
    }




    @GetMapping("/report/conversion-rate")
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

    @PutMapping("/lead/assign/{id}")
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
    }






    @GetMapping("/lead/my")
    public List<LeadmatrixEntity> myLead(@RequestParam String email) {
        return leadmatrixRepository.findByAssignedTo(email);
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



    /*@GetMapping("/dashboard/sales-performance")
    public long salesPerformance(@RequestParam String email){
        return leadmatrixRepository.countByAssignedTo(email);
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
    }




// get lead by company
    @GetMapping("/company/leads/{companyId}")
    public List<LeadmatrixEntity> companyLeads(@PathVariable Long companyId) {

        return leadmatrixRepository.findByCompanyId(companyId);
    }
    @GetMapping("/company/plan/{companyId}")
    public ResponseEntity<?> getCompanyPlan(@PathVariable Long companyId) {
        Optional<Subscription> sub = subscriptionRepository.findByCompanyId(companyId);
        return ResponseEntity.ok(sub.orElse(null));
    }

    // Register Lead ////////////////////////////////////////////////////////////
    @PostMapping("/registerlead")
    public LeadmatrixEntity registerLead(@RequestBody LeadmatrixEntity lead) {
        return leadServices.saveLead(lead);
    }

    // Get Lead By Id
    @GetMapping("/lead/{id}")
    public LeadmatrixEntity getLead(@PathVariable Long id) {
        return leadServices.getLeadById(id);
    }

    // Delete Lead
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
    }*/
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
    }



    @Autowired
    private LeadTaskRepository leadTaskRepository;

    @PostMapping("/lead/task")
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
    }





    @PostMapping("/lead/activity")///  ////////////////////
    public ResponseEntity<?> addActivity(@RequestBody LeadActivity activity) {
        activity.setActivityDate(LocalDate.now().toString());
        return ResponseEntity.ok(activityRepository.save(activity));
    }

    @GetMapping("/lead/activity/{leadId}")
    public List<LeadActivity> getLeadActivityByLeadId(@PathVariable Long leadId){

        return activityRepository.findByLeadId(leadId);

    }


    @PostMapping("/lead/reminder")/// //////////////////////////////
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
    }





    @PostMapping("/lead/note")/// //////////////////////////////////
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
    }


    @Autowired
    private ExcelLeadService excelLeadService;

    @PostMapping("/lead/import")
    public String importLeads(
            @RequestParam("file") MultipartFile file) {

        try {
            excelLeadService.importLeads(file);
            return "Leads imported successfully";
        } catch (Exception e) {
            return "Error importing file";
        }
    }


    // get hot leads api
    @GetMapping("/lead/hot")
    public List<LeadmatrixEntity> hotLeads(){
        return leadmatrixRepository.findByScoreGreaterThan(70);
    }


        @Autowired
        private TwilioService twilioService;

        @PostMapping("/send-whatsapp")
        public String sendMessage(@RequestParam String phone) {
            twilioService.sendWhatsAppMessage(
                    phone,
                    "Hello from CRM 🚀"
            );
            return "Message Sent";
        }




    }


