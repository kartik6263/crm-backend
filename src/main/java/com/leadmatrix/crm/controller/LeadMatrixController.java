package com.leadmatrix.crm.controller;


import com.leadmatrix.crm.entity.LeadActivity;
import com.leadmatrix.crm.entity.LeadNote;
import com.leadmatrix.crm.entity.LeadReminder;
import com.leadmatrix.crm.entity.LeadmatrixEntity;
import com.leadmatrix.crm.respository.ActivityRepository;
import com.leadmatrix.crm.respository.LeadNoteRepository;
import com.leadmatrix.crm.respository.LeadmatrixRespository;
import com.leadmatrix.crm.respository.ReminderRepository;
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

@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class LeadMatrixController {


    @Autowired
    private leadServices leadServices;

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

    // Add Lead  ///////////////////////////////////
    @PostMapping("/add")
    public ResponseEntity<?> createLead(@RequestBody LeadmatrixEntity lead) {
        if (lead.getStatus() == null || lead.getStatus().isEmpty()) {
            lead.setStatus("NEW");
        }
        return ResponseEntity.ok(leadServices.saveLead(lead));
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
        LeadmatrixEntity lead = leadServices.getLeadById(id);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }
        lead.setStatus(status);
        leadmatrixRepository.save(lead);

        if ("CUSTOMER".equalsIgnoreCase(status) && lead.getEmail() != null && !lead.getEmail().isEmpty()) {
            emailService.sendEmail(
                    lead.getEmail(),
                    "Congratulations",
                    "Thank you for becoming our customer."
            );
        }
        return ResponseEntity.ok("Lead status updated successfully");
    }

    @PostMapping("/lead/upload/{id}")
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
    public double conversionRate(){

        long total = leadmatrixRepository.count();

        long customers = leadmatrixRepository.countByStatus("CUSTOMER");

        return (customers * 100.0) / total;

    }

    @GetMapping("/report/source")
    public long leadsBySource(@RequestParam String source){

        return leadmatrixRepository.countBySource(source);
    }

    @GetMapping("/report/sales")
    public long salePerformance(@RequestParam String email){

        return leadmatrixRepository.countByAssignedTo(email);
    }

    @GetMapping("/report/date")
    public long leadsByDate(@RequestParam String date){

        return leadmatrixRepository.countByCreatedDate(date);
    }



    @PutMapping("/lead/assign/{id}")
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

    @GetMapping("/dashboard/sales-performance")
    public long salesPerformance(@RequestParam String email){
        return leadmatrixRepository.countByAssignedTo(email);
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
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody LeadmatrixEntity newLead) {
        LeadmatrixEntity updated = leadServices.updateLead(id, newLead);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
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
        return ResponseEntity.ok(reminderRepository.save(reminder));
    }

    @GetMapping("/lead/reminder/{leadId}")
    public List<LeadReminder> getReminderByLeadId(@PathVariable Long leadId) {
        return reminderRepository.findByLeadId(leadId);
    }

    @GetMapping("/lead/reminder/{date}")
    public List<LeadReminder> getReminders(@PathVariable String date){

        return reminderRepository.findByReminderDate(date);
    }



    @PostMapping("/lead/note")/// //////////////////////////////////
    public ResponseEntity<?> addNote(@RequestBody LeadNote note) {
        note.setCreatedDate(LocalDate.now().toString());
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


