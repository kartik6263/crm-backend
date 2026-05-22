package com.resolion.crm.entity;

import com.resolion.crm.enums.*;
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
        name = "meetings",
        indexes = {
                @Index(name = "idx_meetings_company_id", columnList = "company_id"),
                @Index(name = "idx_meetings_host_email", columnList = "host_email"),
                @Index(name = "idx_meetings_created_by", columnList = "created_by"),
                @Index(name = "idx_meetings_start_time", columnList = "start_time"),
                @Index(name = "idx_meetings_related_type", columnList = "related_type")
        }
)
public class MeetingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= BASIC INFO =================

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_venue", length = 80)
    private MeetingVenue meetingVenue;

    @Column(length = 255)
    private String location;

    @Column(name = "all_day")
    private Boolean allDay;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime fromDateTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime toDateTime;

    @Column(name = "host_name", length = 150)
    private String hostName;

    @Column(name = "host_email", length = 150)
    private String hostEmail;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ================= PARTICIPANTS =================

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_type", length = 50)
    private MeetingParticipantType participantType;

    // Store selected IDs as CSV: 1,2,3
    @Column(name = "participant_ids", columnDefinition = "TEXT")
    private String participantIds;

    // Store names as CSV or JSON string if needed
    @Column(name = "participant_names", columnDefinition = "TEXT")
    private String participantNames;

    // Store invited emails as CSV
    @Column(name = "participant_emails", columnDefinition = "TEXT")
    private String participantEmails;

    @Column(name = "show_contacts_without_email")
    private Boolean showContactsWithoutEmail;

    @Column(name = "show_leads_without_email")
    private Boolean showLeadsWithoutEmail;

    @Column(name = "show_users_without_email")
    private Boolean showUsersWithoutEmail;

    // ================= RELATED TO =================

    @Enumerated(EnumType.STRING)
    @Column(name = "related_type", length = 80)
    private MeetingRelatedType relatedType;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "related_name", length = 200)
    private String relatedName;

    // ================= REMINDERS =================

    @Enumerated(EnumType.STRING)
    @Column(name = "participants_reminder", length = 80)
    private MeetingReminderType participantsReminder;

    @Enumerated(EnumType.STRING)
    @Column(name = "host_reminder", length = 80)
    private MeetingReminderType reminder;

    // Optional second reminder
    @Enumerated(EnumType.STRING)
    @Column(name = "second_reminder", length = 80)
    private MeetingReminderType secondReminder;

    // ================= REPEAT =================

    @Column(name = "repeat_enabled")
    private Boolean repeatEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", length = 50)
    private MeetingRepeatType repeatType;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_frequency", length = 50)
    private MeetingRepeatFrequency repeatFrequency;

    // every 1 day, every 2 weeks, every 3 months
    @Column(name = "repeat_every")
    private Integer repeatEvery;

    // Weekly selected days: MONDAY,WEDNESDAY,FRIDAY
    @Column(name = "repeat_week_days", length = 255)
    private String repeatWeekDays;

    // For yearly / quarterly / biannually: JAN,JUL,DEC
    @Column(name = "repeat_months", length = 255)
    private String repeatMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "monthly_repeat_mode", length = 50)
    private MeetingMonthlyRepeatMode monthlyRepeatMode;

    // ON_DAY mode: 1 to 31
    @Column(name = "repeat_month_day")
    private Integer repeatMonthDay;

    // ON_THE mode: FIRST / SECOND / LAST
    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_week_order", length = 50)
    private MeetingWeekOrder repeatWeekOrder;

    // ON_THE mode: MONDAY / TUESDAY etc.
    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_week_day", length = 50)
    private MeetingWeekDay repeatWeekDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_end_type", length = 50)
    private MeetingRepeatEndType repeatEndType;

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

        if (meetingVenue == null) {
            meetingVenue = MeetingVenue.NONE;
        }

        if (allDay == null) {
            allDay = false;
        }

        if (participantType == null) {
            participantType = MeetingParticipantType.NONE;
        }

        if (relatedType == null) {
            relatedType = MeetingRelatedType.NONE;
        }

        if (participantsReminder == null) {
            participantsReminder = MeetingReminderType.NONE;
        }

        if (reminder == null) {
            reminder = MeetingReminderType.NONE;
        }

        if (secondReminder == null) {
            secondReminder = MeetingReminderType.NONE;
        }

        if (repeatEnabled == null) {
            repeatEnabled = false;
        }

        if (repeatType == null) {
            repeatType = MeetingRepeatType.NONE;
        }

        if (repeatEvery == null) {
            repeatEvery = 1;
        }

        if (repeatEndType == null) {
            repeatEndType = MeetingRepeatEndType.NEVER;
        }

        if (showContactsWithoutEmail == null) {
            showContactsWithoutEmail = false;
        }

        if (showLeadsWithoutEmail == null) {
            showLeadsWithoutEmail = false;
        }

        if (showUsersWithoutEmail == null) {
            showUsersWithoutEmail = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}