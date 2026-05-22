package com.resolion.crm.services;

import com.resolion.crm.ENUMS.CompanyRole;
import com.resolion.crm.controller.NotificationController;
import com.resolion.crm.entity.LeadmatrixEntity;

import com.resolion.crm.respository.LeadmatrixRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@Service
public class leadServices {


    @Autowired
    private LeadmatrixRespository leadmatrixRespository;

    @Autowired
    private  LeadScoringService leadScoringService;

    @Autowired
    LeadmatrixRespository leadmatrixRepository;

    @Autowired
    CompanyAccessService companyAccessService;

    public LeadmatrixEntity saveLead(LeadmatrixEntity lead) {

        //calculate score
        lead.setScore(leadScoringService.calculateScore(lead));
        // save lead
        return leadmatrixRespository.save(lead);
    }

    public LeadmatrixEntity createLead(LeadmatrixEntity lead) {
        if (lead.getStatus() == null || lead.getStatus().isBlank()) {
            lead.setStatus("NEW");
        }

        lead.setScore(leadScoringService.calculateScore(lead));
        return leadmatrixRepository.save(lead);
    }
   /* public List<LeadmatrixEntity> getVisibleLeadsForUser(String email) {
        databaseCRM user = crmRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return leadmatrixRespository.findByCompanyId(user.getCompanyId());
        }
        if ("SALES".equalsIgnoreCase(user.getRole())) {
            return leadmatrixRespository.findByCompanyIdAndAssignedTo(user.getCompanyId(), user.getEmail());
        }
        return leadmatrixRespository.findByCompanyIdAndCreatedBy(user.getCompanyId(), user.getEmail());
    }*/
   public List<LeadmatrixEntity> getVisibleLeadsForUser(String email, Long companyId) {
       if (!companyAccessService.hasCompanyAccess(email, companyId)) {
           throw new RuntimeException("No company access");
       }

       CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

       if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
           return leadmatrixRepository.findByCompanyId(companyId);
       }
       if (role == CompanyRole.SALES) {
           return leadmatrixRepository.findByCompanyIdAndAssignedTo(companyId, email);
       }
       return leadmatrixRepository.findByCompanyIdAndCreatedBy(companyId, email);
      // return leadmatrixRepository.findByCompanyId(companyId);
   }

    // Get All Leads
    public List<LeadmatrixEntity> getAllLeads() {
        return leadmatrixRespository.findAll();
    }




    // Get Lead By Id
    public LeadmatrixEntity getLeadById(Long id) {
        return leadmatrixRespository.findById(id).orElse(null);
    }

    // Delete Lead
    public void deleteLead(Long id) {
        leadmatrixRespository.deleteById(id);
    }

    // Update Lead
    public LeadmatrixEntity updateLead(Long id, LeadmatrixEntity newLead) {

        LeadmatrixEntity oldLead = leadmatrixRespository.findById(id).orElse(null);

        if (oldLead == null) {
            return null;
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
        oldLead.setLastActivity(newLead.getLastActivity());

        oldLead.setScore(leadScoringService.calculateScore(oldLead));

        return leadmatrixRepository.save(oldLead);

    }

    public LeadmatrixEntity updateLeadStatus(Long id, String status) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);

        if (lead == null) {
            return null;
        }

        lead.setStatus(status);
        lead.setScore(leadScoringService.calculateScore(lead));

        return leadmatrixRepository.save(lead);
    }

    public LeadmatrixEntity assignLead(Long id, String salesEmail) {
        LeadmatrixEntity lead = leadmatrixRepository.findById(id).orElse(null);

        if (lead == null) {
            return null;
        }

        lead.setAssignedTo(salesEmail);
        return leadmatrixRepository.save(lead);
    }

    public List<LeadmatrixEntity> getHotLeads(Long companyId) {
        return leadmatrixRepository.findByCompanyIdAndScoreGreaterThan(companyId, 70);
    }

    public long countCompanyLeads(Long companyId) {
        return leadmatrixRepository.countByCompanyId(companyId);
    }

    public long countCompanyLeadsByStatus(Long companyId, String status) {
        return leadmatrixRepository.countByCompanyIdAndStatusIgnoreCase(companyId, status);
    }

    public double getCompanyConversionRate(Long companyId) {
        long total = leadmatrixRepository.countByCompanyId(companyId);
        long customers = leadmatrixRepository.countByCompanyIdAndStatusIgnoreCase(companyId, "CUSTOMER");

        if (total == 0) {
            return 0;
        }

        return (customers * 100.0) / total;
    }




    @Autowired
    private NotificationController notificationController;
 // notification controller
    public void assignLead() {
        notificationController.sendNotification("New lead assigned to sales team");
    }

//// manager and admin can views all lead ( manager api)
//    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
//    @GetMapping("/report/all")
//    public List<LeadmatrixEntity> allLeads(){
//        return leadmatrixRespository.findAll();
//    }
//
//    // (sales api) salespeople can only view their assigned leads
//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/sales/leads/{email}")
//    public List<LeadmatrixEntity> salesLeads(@PathVariable String email){
//        return leadmatrixRespository.findByAssignedTo(email);
//    }


}

