package com.resolion.crm.service;

import com.resolion.crm.enums.*;

import com.resolion.crm.dto.TaskRequest;
import com.resolion.crm.entity.TaskEntity;
import com.resolion.crm.dto.TaskResponse;
import com.resolion.crm.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository leadtaskRepository;

    @Autowired
    private CompanyAccessService companyAccessService;



    public TaskResponse createTask(String email, Long companyId, TaskRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        TaskEntity task = TaskEntity.builder()
                .taskTitle(request.getTaskTitle())
                .subject(request.getSubject())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .taskOwner(request.getTaskOwner() == null || request.getTaskOwner().isBlank()
                        ? email
                        : request.getTaskOwner())
                .assignedTo(request.getAssignedTo() == null || request.getAssignedTo().isBlank()
                        ? email
                        : request.getAssignedTo())
                .description(request.getDescription())

                .leadId(request.getLeadId())

                .relatedContactType(request.getRelatedContactType())
                .relatedContactId(request.getRelatedContactId())
                .relatedContactName(request.getRelatedContactName())

                .relatedModuleType(request.getRelatedModuleType())
                .relatedModuleId(request.getRelatedModuleId())
                .relatedModuleName(request.getRelatedModuleName())

//                .reminderEnabled(request.getReminderEnabled())
//                .reminderDate(request.getReminderDate())
//                .reminderDateTime(request.getReminderDateTime())
//                .notifyType(request.getNotifyType())
                .reminderEnabled(request.getReminderEnabled())
                .reminderType(request.getReminderType())
                .reminderBeforeValue(request.getReminderBeforeValue())
                .reminderBeforeUnit(request.getReminderBeforeUnit())
                .reminderDate(request.getReminderDate())
                .reminderDateTime(request.getReminderDateTime())
                .notifyType(request.getNotifyType())
                .reminderRepeatEnabled(request.getReminderRepeatEnabled())
                .reminderRepeatType(request.getReminderRepeatType())
                .reminderRepeatEvery(request.getReminderRepeatEvery())
                .reminderRepeatMonths(request.getReminderRepeatMonths())
                .reminderRepeatEndDate(request.getReminderRepeatEndDate())

                .repeatEnabled(request.getRepeatEnabled())
                .repeatType(request.getRepeatType())
                .repeatFrequency(request.getRepeatFrequency())
                .repeatEvery(request.getRepeatEvery())
                .repeatWeekDays(request.getRepeatWeekDays())
                .repeatMonths(request.getRepeatMonths())
                .monthlyRepeatMode(request.getMonthlyRepeatMode())
                .repeatMonthDay(request.getRepeatMonthDay())
                .repeatWeekOrder(request.getRepeatWeekOrder())
                .repeatWeekDay(request.getRepeatWeekDay())
                .repeatEndType(request.getRepeatEndType())
                .repeatEndAfterTimes(request.getRepeatEndAfterTimes())
                .repeatEndDate(request.getRepeatEndDate())

                .companyId(companyId)
                .createdBy(email)
                .build();

        validateReminder(task);
        validateRepeat(task);

        TaskEntity saved = leadtaskRepository.save(task);
        return toResponse(saved);
    }





    public List<TaskResponse> getVisibleTasks(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<TaskEntity> tasks;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            tasks = leadtaskRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            tasks = leadtaskRepository.findByCompanyIdAndAssignedToOrderByIdDesc(companyId, email);
        } else {
            tasks = leadtaskRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(String email, Long id) {
        TaskEntity task = leadtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        validateAccess(email, task.getCompanyId());

        return toResponse(task);
    }

    public TaskResponse updateTask(String email, Long id, TaskRequest request) {
        TaskEntity task = leadtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        validateAccess(email, task.getCompanyId());
        validateRequest(request);

        task.setTaskTitle(request.getTaskTitle());
        task.setSubject(request.getSubject());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setTaskOwner(request.getTaskOwner());
        task.setAssignedTo(request.getAssignedTo());
        task.setDescription(request.getDescription());

        task.setLeadId(request.getLeadId());

        task.setRelatedContactType(request.getRelatedContactType());
        task.setRelatedContactId(request.getRelatedContactId());
        task.setRelatedContactName(request.getRelatedContactName());

        task.setRelatedModuleType(request.getRelatedModuleType());
        task.setRelatedModuleId(request.getRelatedModuleId());
        task.setRelatedModuleName(request.getRelatedModuleName());

//        task.setReminderEnabled(request.getReminderEnabled());
//        task.setReminderDate(request.getReminderDate());
//        task.setReminderDateTime(request.getReminderDateTime());
//        task.setNotifyType(request.getNotifyType());
        task.setReminderEnabled(request.getReminderEnabled());
        task.setReminderType(request.getReminderType());
        task.setReminderBeforeValue(request.getReminderBeforeValue());
        task.setReminderBeforeUnit(request.getReminderBeforeUnit());
        task.setReminderDate(request.getReminderDate());
        task.setReminderDateTime(request.getReminderDateTime());
        task.setNotifyType(request.getNotifyType());
        task.setReminderRepeatEnabled(request.getReminderRepeatEnabled());
        task.setReminderRepeatType(request.getReminderRepeatType());
        task.setReminderRepeatEvery(request.getReminderRepeatEvery());
        task.setReminderRepeatMonths(request.getReminderRepeatMonths());
        task.setReminderRepeatEndDate(request.getReminderRepeatEndDate());

        task.setRepeatEnabled(request.getRepeatEnabled());
        task.setRepeatType(request.getRepeatType());
        task.setRepeatFrequency(request.getRepeatFrequency());
        task.setRepeatEvery(request.getRepeatEvery());
        task.setRepeatWeekDays(request.getRepeatWeekDays());
        task.setRepeatMonths(request.getRepeatMonths());
        task.setMonthlyRepeatMode(request.getMonthlyRepeatMode());
        task.setRepeatMonthDay(request.getRepeatMonthDay());
        task.setRepeatWeekOrder(request.getRepeatWeekOrder());
        task.setRepeatWeekDay(request.getRepeatWeekDay());
        task.setRepeatEndType(request.getRepeatEndType());
        task.setRepeatEndAfterTimes(request.getRepeatEndAfterTimes());
        task.setRepeatEndDate(request.getRepeatEndDate());

        validateReminder(task);
        validateRepeat(task);

        TaskEntity saved = leadtaskRepository.save(task);
        return toResponse(saved);
    }

    public TaskResponse updateTaskStatus(String email, Long id, TaskStatus status) {
        TaskEntity task = leadtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        validateAccess(email, task.getCompanyId());

        task.setStatus(status);

        TaskEntity saved = leadtaskRepository.save(task);
        return toResponse(saved);
    }

    public void deleteTask(String email, Long id) {
        TaskEntity task = leadtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        validateAccess(email, task.getCompanyId());

        leadtaskRepository.delete(task);
    }

    public List<TaskResponse> getTasksByLead(String email, Long companyId, Long leadId) {
        validateAccess(email, companyId);

        return leadtaskRepository.findByCompanyIdAndLeadIdOrderByIdDesc(companyId, leadId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByStatus(String email, Long companyId, TaskStatus status) {
        validateAccess(email, companyId);

        return leadtaskRepository.findByCompanyIdAndStatusOrderByIdDesc(companyId, status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByDueDate(String email, Long companyId, LocalDate dueDate) {
        validateAccess(email, companyId);

        return leadtaskRepository.findByCompanyIdAndDueDateOrderByIdDesc(companyId, dueDate)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countTasks(String email, Long companyId) {
        validateAccess(email, companyId);
        return leadtaskRepository.countByCompanyId(companyId);
    }

    public long countTasksByStatus(String email, Long companyId, TaskStatus status) {
        validateAccess(email, companyId);
        return leadtaskRepository.countByCompanyIdAndStatus(companyId, status);
    }

    public long countTodayTasks(String email, Long companyId) {
        validateAccess(email, companyId);
        return leadtaskRepository.countByCompanyIdAndDueDate(companyId, LocalDate.now());
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateReminder(TaskEntity task) {
        if (!Boolean.TRUE.equals(task.getReminderEnabled())) {
            task.setReminderType(TaskReminderType.NONE);
            task.setReminderDate(null);
            task.setReminderDateTime(null);
            task.setReminderBeforeValue(null);
            task.setReminderBeforeUnit(null);
            task.setReminderRepeatEnabled(false);
            task.setReminderRepeatType(TaskReminderRepeatType.NONE);
            return;
        }

        if (task.getReminderType() == null) {
            throw new RuntimeException("Reminder type is required");
        }

        if (task.getNotifyType() == null) {
            task.setNotifyType(TaskNotifyType.POP_UP);
        }

        if (task.getReminderType() == TaskReminderType.ON_DUE_DATE) {
            if (task.getDueDate() == null) {
                throw new RuntimeException("Due date is required for on due date reminder");
            }
        }

        if (task.getReminderType() == TaskReminderType.BEFORE_DUE_DATE) {
            if (task.getDueDate() == null) {
                throw new RuntimeException("Due date is required for before due date reminder");
            }

            if (task.getReminderBeforeValue() == null || task.getReminderBeforeValue() < 1) {
                throw new RuntimeException("Reminder before value must be at least 1");
            }

            if (task.getReminderBeforeUnit() == null) {
                throw new RuntimeException("Reminder before unit is required");
            }
        }

        if (task.getReminderType() == TaskReminderType.CUSTOM) {
            if (task.getReminderDateTime() == null) {
                throw new RuntimeException("Custom reminder date and time is required");
            }

            if (task.getReminderDate() == null) {
                task.setReminderDate(task.getReminderDateTime().toLocalDate());
            }
        }

        if (Boolean.TRUE.equals(task.getReminderRepeatEnabled())) {
            if (task.getReminderRepeatType() == null || task.getReminderRepeatType() == TaskReminderRepeatType.NONE) {
                throw new RuntimeException("Reminder repeat type is required");
            }

            if (task.getReminderRepeatEvery() == null || task.getReminderRepeatEvery() < 1) {
                throw new RuntimeException("Reminder repeat every must be at least 1");
            }
            if (
                    task.getReminderRepeatType() == TaskReminderRepeatType.YEARLY ||
                            task.getReminderRepeatType() == TaskReminderRepeatType.QUARTERLY ||
                            task.getReminderRepeatType() == TaskReminderRepeatType.BIANNUALLY
            ) {
                if (task.getReminderRepeatMonths() == null || task.getReminderRepeatMonths().isBlank()) {
                    throw new RuntimeException("Select at least one reminder repeat month");
                }
            }
        } else {
            task.setReminderRepeatType(TaskReminderRepeatType.NONE);
            task.setReminderRepeatEvery(1);
            task.setReminderRepeatEndDate(null);
        }
    }


    private void validateRepeat(TaskEntity task) {
        if (!Boolean.TRUE.equals(task.getRepeatEnabled())) {
            task.setRepeatType(TaskRepeatType.NONE);
            return;
        }

        if (task.getRepeatType() == null) {
            throw new RuntimeException("Repeat type is required");
        }

        if (task.getRepeatType() == TaskRepeatType.CUSTOM) {
            if (task.getRepeatFrequency() == null) {
                throw new RuntimeException("Repeat frequency is required for custom repeat");
            }

            if (task.getRepeatEvery() == null || task.getRepeatEvery() < 1) {
                throw new RuntimeException("Repeat every must be at least 1");
            }

            if (task.getRepeatFrequency() == TaskRepeatFrequency.WEEKLY) {
                if (task.getRepeatWeekDays() == null || task.getRepeatWeekDays().isBlank()) {
                    throw new RuntimeException("Select at least one weekday");
                }
            }



            if (
                    task.getRepeatFrequency() == TaskRepeatFrequency.MONTHLY ||
                            task.getRepeatFrequency() == TaskRepeatFrequency.YEARLY ||
                            task.getRepeatFrequency() == TaskRepeatFrequency.QUARTERLY ||
                            task.getRepeatFrequency() == TaskRepeatFrequency.BIANNUALLY
            ) {

                if (task.getMonthlyRepeatMode() == null) {
                    throw new RuntimeException("Monthly repeat mode is required");
                }

                if (
                        task.getRepeatFrequency() == TaskRepeatFrequency.YEARLY ||
                                task.getRepeatFrequency() == TaskRepeatFrequency.QUARTERLY ||
                                task.getRepeatFrequency() == TaskRepeatFrequency.BIANNUALLY
                ) {
                    if (task.getRepeatMonths() == null || task.getRepeatMonths().isBlank()) {
                        throw new RuntimeException("Select at least one month");
                    }
                }

                if (task.getMonthlyRepeatMode() == TaskMonthlyRepeatMode.ON_DAY) {
                    if (task.getRepeatMonthDay() == null || task.getRepeatMonthDay() < 1 || task.getRepeatMonthDay() > 31) {
                        throw new RuntimeException("Repeat month day must be between 1 and 31");
                    }
                }

                if (task.getMonthlyRepeatMode() == TaskMonthlyRepeatMode.ON_THE) {
                    if (task.getRepeatWeekOrder() == null || task.getRepeatWeekDay() == null) {
                        throw new RuntimeException("Week order and weekday are required");
                    }
                }
            }
        }

        if (task.getRepeatEndType() == null) {
            task.setRepeatEndType(TaskRepeatEndType.NEVER);
        }

        if (task.getRepeatEndType() == TaskRepeatEndType.AFTER) {
            if (task.getRepeatEndAfterTimes() == null || task.getRepeatEndAfterTimes() < 1) {
                throw new RuntimeException("Repeat end after times must be at least 1");
            }
        }

        if (task.getRepeatEndType() == TaskRepeatEndType.ON_DATE) {
            if (task.getRepeatEndDate() == null) {
                throw new RuntimeException("Repeat end date is required");
            }
        }

        if (
                task.getRepeatFrequency() == TaskRepeatFrequency.YEARLY ||
                        task.getRepeatFrequency() == TaskRepeatFrequency.QUARTERLY ||
                        task.getRepeatFrequency() == TaskRepeatFrequency.BIANNUALLY
        ) {
            if (task.getRepeatMonths() == null || task.getRepeatMonths().isBlank()) {
                throw new RuntimeException("Select at least one month");
            }

            if (task.getMonthlyRepeatMode() == null) {
                throw new RuntimeException("Repeat mode is required");
            }

            if (task.getMonthlyRepeatMode() == TaskMonthlyRepeatMode.ON_DAY) {
                if (task.getRepeatMonthDay() == null || task.getRepeatMonthDay() < 1 || task.getRepeatMonthDay() > 31) {
                    throw new RuntimeException("Repeat month day must be between 1 and 31");
                }
            }

            if (task.getMonthlyRepeatMode() == TaskMonthlyRepeatMode.ON_THE) {
                if (task.getRepeatWeekOrder() == null || task.getRepeatWeekDay() == null) {
                    throw new RuntimeException("Week order and weekday are required");
                }
            }
        }
    }

    private void validateRequest(TaskRequest request) {
        if (request.getTaskTitle() == null || request.getTaskTitle().isBlank()) {
            throw new RuntimeException("Task title is required");
        }
    }

    private TaskResponse toResponse(TaskEntity task) {
        return TaskResponse.builder()
                .id(task.getId())
                .taskTitle(task.getTaskTitle())
                .subject(task.getSubject())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .taskOwner(task.getTaskOwner())
                .assignedTo(task.getAssignedTo())
                .description(task.getDescription())

                .leadId(task.getLeadId())

                .relatedContactType(task.getRelatedContactType())
                .relatedContactId(task.getRelatedContactId())
                .relatedContactName(task.getRelatedContactName())

                .relatedModuleType(task.getRelatedModuleType())
                .relatedModuleId(task.getRelatedModuleId())
                .relatedModuleName(task.getRelatedModuleName())

//                .reminderEnabled(task.getReminderEnabled())
//                .reminderDate(task.getReminderDate())
//                .reminderDateTime(task.getReminderDateTime())
//                .notifyType(task.getNotifyType())
                .reminderEnabled(task.getReminderEnabled())
                .reminderType(task.getReminderType())
                .reminderBeforeValue(task.getReminderBeforeValue())
                .reminderBeforeUnit(task.getReminderBeforeUnit())
                .reminderDate(task.getReminderDate())
                .reminderDateTime(task.getReminderDateTime())
                .notifyType(task.getNotifyType())
                .reminderRepeatEnabled(task.getReminderRepeatEnabled())
                .reminderRepeatType(task.getReminderRepeatType())
                .reminderRepeatEvery(task.getReminderRepeatEvery())
                .reminderRepeatMonths(task.getReminderRepeatMonths())
                .reminderRepeatEndDate(task.getReminderRepeatEndDate())

                .repeatEnabled(task.getRepeatEnabled())
                .repeatType(task.getRepeatType())
                .repeatFrequency(task.getRepeatFrequency())
                .repeatEvery(task.getRepeatEvery())
                .repeatWeekDays(task.getRepeatWeekDays())
                .repeatMonths(task.getRepeatMonths())
                .monthlyRepeatMode(task.getMonthlyRepeatMode())
                .repeatMonthDay(task.getRepeatMonthDay())
                .repeatWeekOrder(task.getRepeatWeekOrder())
                .repeatWeekDay(task.getRepeatWeekDay())
                .repeatEndType(task.getRepeatEndType())
                .repeatEndAfterTimes(task.getRepeatEndAfterTimes())
                .repeatEndDate(task.getRepeatEndDate())

                .companyId(task.getCompanyId())
                .createdBy(task.getCreatedBy())
                .createdDate(task.getCreatedDate())
                .updatedDate(task.getUpdatedDate())
                .build();
    }
}