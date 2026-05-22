package com.resolion.crm.dto;

import com.resolion.crm.enums.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRequest {

    private String title;

    private MeetingVenue meetingVenue;
    private String location;

    private Boolean allDay;

    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;

    private String hostName;
    private String hostEmail;

    private MeetingParticipantType participantType;
    private String participantIds;
    private String participantNames;
    private String participantEmails;

    private Boolean showContactsWithoutEmail;
    private Boolean showLeadsWithoutEmail;
    private Boolean showUsersWithoutEmail;

    private MeetingRelatedType relatedType;
    private Long relatedId;
    private String relatedName;

    private MeetingReminderType participantsReminder;
    private MeetingReminderType reminder;
    private MeetingReminderType secondReminder;

    private Boolean repeatEnabled;
    private MeetingRepeatType repeatType;
    private MeetingRepeatFrequency repeatFrequency;
    private Integer repeatEvery;
    private String repeatWeekDays;
    private String repeatMonths;

    private MeetingMonthlyRepeatMode monthlyRepeatMode;
    private Integer repeatMonthDay;
    private MeetingWeekOrder repeatWeekOrder;
    private MeetingWeekDay repeatWeekDay;

    private MeetingRepeatEndType repeatEndType;
    private Integer repeatEndAfterTimes;
    private LocalDate repeatEndDate;

    private String description;
}