package com.resolion.crm.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "lead_tasks")
public class LeadTask {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long leadId;
        private String taskTitle;
        private String taskStatus; // PENDING, DONE
        private String assignedTo;
        private String dueDate;
       // private Long companyId;

        public Long getId() { return id; }

        public Long getLeadId() { return leadId; }
        public void setLeadId(Long leadId) { this.leadId = leadId; }

        public String getTaskTitle() { return taskTitle; }
        public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

        public String getTaskStatus() { return taskStatus; }
        public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }

        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

        public String getDueDate() { return dueDate; }
        public void setDueDate(String dueDate) { this.dueDate = dueDate; }


     //  public Long getCompanyId() {
               // return companyId;
     //   }
       // public void setCompanyId(Long companyId) {
             //   this.companyId = companyId;
       // }

}



