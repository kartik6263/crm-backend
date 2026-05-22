package com.resolion.crm.dpo;

import com.resolion.crm.ENUMS.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    private String taskTitle;
    private TaskSubject subject;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private String taskOwner;
    private String assignedTo;
    private String description;



    private Long leadId;

    private TaskRelatedType relatedContactType;
    private Long relatedContactId;
    private String relatedContactName;

    private TaskRelatedType relatedModuleType;
    private Long relatedModuleId;
    private String relatedModuleName;

//    private Boolean reminderEnabled;
//    private LocalDate reminderDate;
//    private LocalDateTime reminderDateTime;
//    private TaskNotifyType notifyType;

    private Boolean repeatEnabled;
    private TaskRepeatType repeatType;
    private TaskRepeatFrequency repeatFrequency;
    private Integer repeatEvery;
    private String repeatWeekDays;

    private String repeatMonths;



    private TaskMonthlyRepeatMode monthlyRepeatMode;
    private Integer repeatMonthDay;
    private TaskWeekOrder repeatWeekOrder;
    private TaskWeekDay repeatWeekDay;

    private TaskRepeatEndType repeatEndType;
    private Integer repeatEndAfterTimes;
    private LocalDate repeatEndDate;



    private Boolean reminderEnabled;
    private TaskReminderType reminderType;

    private Integer reminderBeforeValue;
    private TaskReminderBeforeUnit reminderBeforeUnit;

    private LocalDate reminderDate;
    private LocalDateTime reminderDateTime;
    private TaskNotifyType notifyType;

    private Boolean reminderRepeatEnabled;
    private TaskReminderRepeatType reminderRepeatType;
    private Integer reminderRepeatEvery;
    private String reminderRepeatMonths;
    private LocalDate reminderRepeatEndDate;
}
