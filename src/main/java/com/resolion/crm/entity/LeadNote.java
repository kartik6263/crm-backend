package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name="lead_notes")

public class LeadNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Long leadId;

    private String noteText;

    private String createdBy;

    private String createdDate;

   // private Long companyId;


    public Long getId() {
        return id;
    }
   //public void setId(Long id) {
      //  this.id = id;
  //  }


    public Long getLeadId() {
        return leadId;
    }
    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }


    public String getNoteText() {
        return noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }


    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }


   // public Long getCompanyId() {
     //   return companyId;
   // }
    //public void setCompanyId(Long companyId) {
     //   this.companyId = companyId;
  //  }

}
