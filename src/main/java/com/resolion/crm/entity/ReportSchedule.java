package com.resolion.crm.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "report_schedules")
public class ReportSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reportId;
    private Long companyId;

    private String userEmail;
    private String emailTo;

    private String frequency;
    private String nextRunTime;

    private boolean active = true;
}