package com.resolion.crm.entity;

import com.resolion.crm.ENUMS.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "tasks",
        indexes = {
                @Index(name = "idx_tasks_company_id", columnList = "company_id"),
                @Index(name = "idx_tasks_lead_id", columnList = "lead_id"),
                @Index(name = "idx_tasks_assigned_to", columnList = "assigned_to"),
                @Index(name = "idx_tasks_status", columnList = "status"),
                @Index(name = "idx_tasks_due_date", columnList = "due_date"),
                @Index(name = "idx_tasks_reminder_enabled", columnList = "reminder_enabled"),
                @Index(name = "idx_tasks_repeat_enabled", columnList = "repeat_enabled")
        }
)
public class TaskEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // ================= BASIC TASK INFO =================

        @Column(name = "task_title", nullable = false, length = 200)
        private String taskTitle;

        @Enumerated(EnumType.STRING)
        @Column(name = "subject", length = 80)
        private TaskSubject subject;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", length = 50)
        private TaskStatus status;

        @Enumerated(EnumType.STRING)
        @Column(name = "priority", length = 50)
        private TaskPriority priority;

        @Column(name = "due_date")
        private LocalDate dueDate;

        @Column(name = "task_owner", length = 150)
        private String taskOwner;

        @Column(name = "assigned_to", length = 150)
        private String assignedTo;

        @Column(columnDefinition = "TEXT")
        private String description;

        // ================= RELATED RECORDS =================

        @Column(name = "lead_id")
        private Long leadId;

        @Enumerated(EnumType.STRING)
        @Column(name = "related_contact_type", length = 50)
        private TaskRelatedType relatedContactType;

        @Column(name = "related_contact_id")
        private Long relatedContactId;

        @Column(name = "related_contact_name", length = 150)
        private String relatedContactName;

        @Enumerated(EnumType.STRING)
        @Column(name = "related_module_type", length = 50)
        private TaskRelatedType relatedModuleType;

        @Column(name = "related_module_id")
        private Long relatedModuleId;

        @Column(name = "related_module_name", length = 150)
        private String relatedModuleName;

        // ================= REMINDER =================

        @Column(name = "reminder_enabled")
        private Boolean reminderEnabled;

        @Enumerated(EnumType.STRING)
        @Column(name = "reminder_type", length = 50)
        private TaskReminderType reminderType;

        @Column(name = "reminder_before_value")
        private Integer reminderBeforeValue;

        @Enumerated(EnumType.STRING)
        @Column(name = "reminder_before_unit", length = 50)
        private TaskReminderBeforeUnit reminderBeforeUnit;

        @Column(name = "reminder_date")
        private LocalDate reminderDate;

        @Column(name = "reminder_date_time")
        private LocalDateTime reminderDateTime;

        @Enumerated(EnumType.STRING)
        @Column(name = "notify_type", length = 50)
        private TaskNotifyType notifyType;

        // Optional: repeated reminder before task is completed
        @Column(name = "reminder_repeat_enabled")
        private Boolean reminderRepeatEnabled;

        @Enumerated(EnumType.STRING)
        @Column(name = "reminder_repeat_type", length = 50)
        private TaskReminderRepeatType reminderRepeatType;

        @Column(name = "reminder_repeat_every")
        private Integer reminderRepeatEvery;

        // For yearly / quarterly / biannually reminder repeat
// Example: JAN,JUL
        @Column(name = "reminder_repeat_months", length = 255)
        private String reminderRepeatMonths;

        @Column(name = "reminder_repeat_end_date")
        private LocalDate reminderRepeatEndDate;

        // ================= REPEAT / RECURRENCE =================


        @Column(name = "repeat_enabled")
        private Boolean repeatEnabled;

        @Enumerated(EnumType.STRING)
        @Column(name = "repeat_type", length = 50)
        private TaskRepeatType repeatType;

        @Enumerated(EnumType.STRING)
        @Column(name = "repeat_frequency", length = 50)
        private TaskRepeatFrequency repeatFrequency;

        // Example: every 2 days, every 3 weeks, every 1 month
        @Column(name = "repeat_every")
        private Integer repeatEvery;

        // Weekly selected days: MONDAY,WEDNESDAY,FRIDAY
        @Column(name = "repeat_week_days", length = 255)
        private String repeatWeekDays;

        // For YEARLY / QUARTERLY / BIANNUALLY custom repeat
// Example: JAN,MAR,SEP
        @Column(name = "repeat_months", length = 255)
        private String repeatMonths;

        @Enumerated(EnumType.STRING)
        @Column(name = "monthly_repeat_mode", length = 50)
        private TaskMonthlyRepeatMode monthlyRepeatMode;

        // ON_DAY mode: 1 to 31
        @Column(name = "repeat_month_day")
        private Integer repeatMonthDay;

        // ON_THE mode: FIRST, SECOND, THIRD, FOURTH, LAST
        @Enumerated(EnumType.STRING)
        @Column(name = "repeat_week_order", length = 50)
        private TaskWeekOrder repeatWeekOrder;

        // ON_THE mode: MONDAY, TUESDAY...
        @Enumerated(EnumType.STRING)
        @Column(name = "repeat_week_day", length = 50)
        private TaskWeekDay repeatWeekDay;

        @Enumerated(EnumType.STRING)
        @Column(name = "repeat_end_type", length = 50)
        private TaskRepeatEndType repeatEndType;

        @Column(name = "repeat_end_after_times")
        private Integer repeatEndAfterTimes;

        @Column(name = "repeat_end_date")
        private LocalDate repeatEndDate;

        // ================= COMPANY / AUDIT =================

        @Column(name = "company_id", nullable = false)
        private Long companyId;

        @Column(name = "created_by", length = 150)
        private String createdBy;

        @Column(name = "created_date")
        private LocalDateTime createdDate;

        @Column(name = "updated_date")
        private LocalDateTime updatedDate;

        @PrePersist
        public void prePersist() {
                LocalDateTime now = LocalDateTime.now();

                if (createdDate == null) {
                        createdDate = now;
                }

                updatedDate = now;

                if (status == null) {
                        status = TaskStatus.PENDING;
                }

                if (priority == null) {
                        priority = TaskPriority.MEDIUM;
                }

                if (subject == null) {
                        subject = TaskSubject.OTHER;
                }

                if (reminderEnabled == null) {
                        reminderEnabled = false;
                }

                if (notifyType == null) {
                        notifyType = TaskNotifyType.POP_UP;
                }

                if (repeatEnabled == null) {
                        repeatEnabled = false;
                }

                if (repeatType == null) {
                        repeatType = TaskRepeatType.NONE;
                }

                if (repeatEvery == null) {
                        repeatEvery = 1;
                }

                if (repeatEndType == null) {
                        repeatEndType = TaskRepeatEndType.NEVER;
                }

                if (relatedContactType == null) {
                        relatedContactType = TaskRelatedType.NONE;
                }

                if (relatedModuleType == null) {
                        relatedModuleType = TaskRelatedType.NONE;
                }

                if (reminderEnabled == null) {
                        reminderEnabled = false;
                }

                if (reminderType == null) {
                        reminderType = TaskReminderType.NONE;
                }

                if (notifyType == null) {
                        notifyType = TaskNotifyType.POP_UP;
                }

                if (reminderRepeatEnabled == null) {
                        reminderRepeatEnabled = false;
                }

                if (reminderRepeatType == null) {
                        reminderRepeatType = TaskReminderRepeatType.NONE;
                }

                if (reminderRepeatEvery == null) {
                        reminderRepeatEvery = 1;
                }
        }

        @PreUpdate
        public void preUpdate() {
                updatedDate = LocalDateTime.now();
        }
}