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
import java.util.List;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class LeadMatrixController {



    @Autowired
    private LeadmatrixRespository leadmatrixRepository;

    // Add Lead  ///////////////////////////////////
    @PostMapping("/add")
    public String addLead(@RequestBody LeadmatrixEntity lead) {
        leadmatrixRepository.save(lead);
        return "Lead Added Successfully";
    }

    // Get All Leads
    @GetMapping("/all")
    public List<LeadmatrixEntity> getAllLeads() {
        return leadmatrixRepository.findAll();
    }

    @GetMapping("/{id}")
    public LeadmatrixEntity getLeadById(@PathVariable Long id) {
        return leadmatrixRepository.findById(id).orElse(null);
    }

    @GetMapping("/search/{name}")
    public List<LeadmatrixEntity> searchLead(@PathVariable String name) {

        return leadmatrixRepository.findByName(name);
    }

    @PutMapping("/lead/status/{id}")
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



    @Autowired
    private EmailService emailService;

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
    public long convertedLeads(){
        return leadmatrixRepository.countByStatus("Customer");
    }

    @GetMapping("/dashboard/lost")
    public long lostLeads(){
        return leadmatrixRepository.countByStatus("Lost");
    }

    @GetMapping("/dashboard/sales-performance")
    public long salesPerformance(@RequestParam String email){

        return leadmatrixRepository.countByAssignedTo(email);
    }



    @Autowired
    private leadServices LeadServices;

// get lead by company
    @GetMapping("/company/leads/{companyId}")
    public List<LeadmatrixEntity> companyLeads(@PathVariable Long companyId) {

        return leadmatrixRepository.findByCompanyId(companyId);
    }

    // Register Lead ////////////////////////////////////////////////////////////
    @PostMapping("/registerlead")
    public LeadmatrixEntity registerLead(@RequestBody LeadmatrixEntity lead) {
        return LeadServices.saveLead(lead);
    }

    // Get Lead By Id
    @GetMapping("/lead/{id}")
    public LeadmatrixEntity getLead(@PathVariable Long id) {
        return LeadServices.getLeadById(id);
    }

    // Delete Lead
    @DeleteMapping("/delete/{id}")
    public String deleteLead(@PathVariable Long id) {
        LeadServices.deleteLead(id);
        return "Lead Deleted Successfully";
    }

    // Update Lead ////////////////////////////////////////////////////////////////////
    @PutMapping("/update/{id}")
    public LeadmatrixEntity updateLead(@PathVariable Long id, @RequestBody LeadmatrixEntity lead) {
        return LeadServices.updateLead(id, lead);
    }





    @Autowired
    private ActivityRepository activityRepository;

    @PostMapping("/lead/activity")///  ////////////////////
    public LeadActivity addActivity(@RequestBody LeadActivity activity){

        return activityRepository.save(activity);

    }

    @GetMapping("/lead/activity/{leadId}")
    public List<LeadActivity> getLeadActivity(@PathVariable Long leadId){

        return activityRepository.findByLeadId(leadId);

    }


    @Autowired
    private ReminderRepository reminderRepository;

    @PostMapping("/lead/reminder")/// //////////////////////////////
    public LeadReminder addReminder(@RequestBody LeadReminder reminder){

        return reminderRepository.save(reminder);
    }

    @GetMapping("/lead/reminder/{date}")
    public List<LeadReminder> getReminders(@PathVariable String date){

        return reminderRepository.findByReminderDate(date);
    }


    @Autowired
    private LeadNoteRepository leadNoteRepository;

    @PostMapping("/lead/note")/// //////////////////////////////////
    public LeadNote addNote(@RequestBody LeadNote note){

        return leadNoteRepository.save(note);
    }

    @GetMapping("/lead/note/{leadId}")
    public List<LeadNote> getNotes(@PathVariable Long leadId){

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


