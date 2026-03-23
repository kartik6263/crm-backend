package com.leadmatrix.crm.services;

import com.leadmatrix.crm.controller.NotificationController;
import com.leadmatrix.crm.entity.LeadmatrixEntity;

import com.leadmatrix.crm.respository.LeadmatrixRespository;
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

    public LeadmatrixEntity saveLead(LeadmatrixEntity lead) {

        //calculate score
        lead.setScore(leadScoringService.calculateScore(lead));
        // save lead
        return leadmatrixRespository.save(lead);
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

        if (oldLead != null) {
            oldLead.setName(newLead.getName());
            oldLead.setEmail(newLead.getEmail());
            oldLead.setPhone(newLead.getPhone());
            return leadmatrixRespository.save(oldLead);
        }

        return null;
    }

    public LeadmatrixEntity createLead(LeadmatrixEntity lead) {

        lead.setStatus("NEW");

        return leadmatrixRespository.save(lead);
    }


    @Autowired
    private NotificationController notificationController;
 // notification controller
    public void assignLead() {
        notificationController.sendNotification("New lead assigned to sales team");
    }

// manager and admin can views all lead ( manager api)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/report/all")
    public List<LeadmatrixEntity> allLeads(){
        return leadmatrixRespository.findAll();
    }

    // (sales api) salespeople can only view their assigned leads
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/sales/leads/{email}")
    public List<LeadmatrixEntity> salesLeads(@PathVariable String email){
        return leadmatrixRespository.findByAssignedTo(email);
    }


}

