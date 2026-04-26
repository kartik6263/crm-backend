package com.resolion.crm.entity;

import jakarta.persistence.*;

@Entity
    @Table(name = "report_schedule")
    public class ReportSchedule {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long companyId;
        private Long reportId;
        private String userEmail;
        private String frequency;
        private String nextRunTime;
        private String emailTo;
        private Boolean active = true;

        // getters setters
    }

