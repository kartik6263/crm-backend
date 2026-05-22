package com.resolion.crm.services;

import com.resolion.crm.ENUMS.*;
import com.resolion.crm.dpo.MeetingRequest;
import com.resolion.crm.dpo.MeetingResponse;
import com.resolion.crm.entity.MeetingEntity;
import com.resolion.crm.respository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    public MeetingResponse createMeeting(String email, Long companyId, MeetingRequest request) {
        validateAccess(email, companyId);
        validateRequest(request);

        MeetingEntity meeting = MeetingEntity.builder()
                .title(request.getTitle())
                .meetingVenue(request.getMeetingVenue())
                .location(request.getLocation())
                .allDay(request.getAllDay())
                .fromDateTime(request.getFromDateTime())
                .toDateTime(request.getToDateTime())
                .hostName(request.getHostName())
                .hostEmail(request.getHostEmail() == null || request.getHostEmail().isBlank()
                        ? email
                        : request.getHostEmail())
                .participantType(request.getParticipantType())
                .participantIds(request.getParticipantIds())
                .participantNames(request.getParticipantNames())
                .participantEmails(request.getParticipantEmails())
                .showContactsWithoutEmail(request.getShowContactsWithoutEmail())
                .showLeadsWithoutEmail(request.getShowLeadsWithoutEmail())
                .showUsersWithoutEmail(request.getShowUsersWithoutEmail())
                .relatedType(request.getRelatedType())
                .relatedId(request.getRelatedId())
                .relatedName(request.getRelatedName())
                .participantsReminder(request.getParticipantsReminder())
                .reminder(request.getReminder())
                .secondReminder(request.getSecondReminder())
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
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(email)
                .build();

        validateRepeat(meeting);

        MeetingEntity saved = meetingRepository.save(meeting);
        return toResponse(saved);
    }

    public List<MeetingResponse> getVisibleMeetings(String email, Long companyId) {
        validateAccess(email, companyId);

        CompanyRole role = companyAccessService.getCompanyRole(email, companyId);

        List<MeetingEntity> meetings;

        if (role == CompanyRole.OWNER || role == CompanyRole.ADMIN) {
            meetings = meetingRepository.findByCompanyIdOrderByIdDesc(companyId);
        } else if (role == CompanyRole.SALES) {
            meetings = meetingRepository.findByCompanyIdAndHostEmailOrderByIdDesc(companyId, email);
        } else {
            meetings = meetingRepository.findByCompanyIdAndCreatedByOrderByIdDesc(companyId, email);
        }

        return meetings.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MeetingResponse getMeetingById(String email, Long id) {
        MeetingEntity meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        validateAccess(email, meeting.getCompanyId());

        return toResponse(meeting);
    }

    public MeetingResponse updateMeeting(String email, Long id, MeetingRequest request) {
        MeetingEntity meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        validateAccess(email, meeting.getCompanyId());
        validateRequest(request);

        meeting.setTitle(request.getTitle());
        meeting.setMeetingVenue(request.getMeetingVenue());
        meeting.setLocation(request.getLocation());
        meeting.setAllDay(request.getAllDay());
        meeting.setFromDateTime(request.getFromDateTime());
        meeting.setToDateTime(request.getToDateTime());
        meeting.setHostName(request.getHostName());
        meeting.setHostEmail(request.getHostEmail());
        meeting.setParticipantType(request.getParticipantType());
        meeting.setParticipantIds(request.getParticipantIds());
        meeting.setParticipantNames(request.getParticipantNames());
        meeting.setParticipantEmails(request.getParticipantEmails());
        meeting.setShowContactsWithoutEmail(request.getShowContactsWithoutEmail());
        meeting.setShowLeadsWithoutEmail(request.getShowLeadsWithoutEmail());
        meeting.setShowUsersWithoutEmail(request.getShowUsersWithoutEmail());
        meeting.setRelatedType(request.getRelatedType());
        meeting.setRelatedId(request.getRelatedId());
        meeting.setRelatedName(request.getRelatedName());
        meeting.setParticipantsReminder(request.getParticipantsReminder());
        meeting.setReminder(request.getReminder());
        meeting.setSecondReminder(request.getSecondReminder());
        meeting.setRepeatEnabled(request.getRepeatEnabled());
        meeting.setRepeatType(request.getRepeatType());
        meeting.setRepeatFrequency(request.getRepeatFrequency());
        meeting.setRepeatEvery(request.getRepeatEvery());
        meeting.setRepeatWeekDays(request.getRepeatWeekDays());
        meeting.setRepeatMonths(request.getRepeatMonths());
        meeting.setMonthlyRepeatMode(request.getMonthlyRepeatMode());
        meeting.setRepeatMonthDay(request.getRepeatMonthDay());
        meeting.setRepeatWeekOrder(request.getRepeatWeekOrder());
        meeting.setRepeatWeekDay(request.getRepeatWeekDay());
        meeting.setRepeatEndType(request.getRepeatEndType());
        meeting.setRepeatEndAfterTimes(request.getRepeatEndAfterTimes());
        meeting.setRepeatEndDate(request.getRepeatEndDate());
        meeting.setDescription(request.getDescription());

        validateRepeat(meeting);

        MeetingEntity saved = meetingRepository.save(meeting);
        return toResponse(saved);
    }

    public void deleteMeeting(String email, Long id) {
        MeetingEntity meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        validateAccess(email, meeting.getCompanyId());

        meetingRepository.delete(meeting);
    }

    public List<MeetingResponse> getMeetingsBetween(String email, Long companyId, LocalDateTime start, LocalDateTime end) {
        validateAccess(email, companyId);

        return meetingRepository.findByCompanyIdAndFromDateTimeBetweenOrderByFromDateTimeAsc(companyId, start, end)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MeetingResponse> getMeetingsByRelated(String email,
                                                      Long companyId,
                                                      MeetingRelatedType relatedType,
                                                      Long relatedId) {
        validateAccess(email, companyId);

        return meetingRepository.findByCompanyIdAndRelatedTypeAndRelatedIdOrderByIdDesc(companyId, relatedType, relatedId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void validateAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateRequest(MeetingRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Meeting title is required");
        }

        if (request.getFromDateTime() == null) {
            throw new RuntimeException("Meeting start date and time is required");
        }

        if (request.getToDateTime() == null) {
            throw new RuntimeException("Meeting end date and time is required");
        }

        if (request.getToDateTime().isBefore(request.getFromDateTime())) {
            throw new RuntimeException("Meeting end time cannot be before start time");
        }
    }

    private void validateRepeat(MeetingEntity meeting) {
        if (!Boolean.TRUE.equals(meeting.getRepeatEnabled())) {
            meeting.setRepeatType(MeetingRepeatType.NONE);
            return;
        }

        if (meeting.getRepeatType() == null || meeting.getRepeatType() == MeetingRepeatType.NONE) {
            throw new RuntimeException("Repeat type is required");
        }

        if (meeting.getRepeatType() == MeetingRepeatType.CUSTOM) {
            if (meeting.getRepeatFrequency() == null) {
                throw new RuntimeException("Repeat frequency is required for custom repeat");
            }

            if (meeting.getRepeatEvery() == null || meeting.getRepeatEvery() < 1) {
                throw new RuntimeException("Repeat every must be at least 1");
            }

            if (meeting.getRepeatFrequency() == MeetingRepeatFrequency.WEEKLY) {
                if (meeting.getRepeatWeekDays() == null || meeting.getRepeatWeekDays().isBlank()) {
                    throw new RuntimeException("Select at least one weekday");
                }
            }

            if (
                    meeting.getRepeatFrequency() == MeetingRepeatFrequency.MONTHLY ||
                            meeting.getRepeatFrequency() == MeetingRepeatFrequency.QUARTERLY ||
                            meeting.getRepeatFrequency() == MeetingRepeatFrequency.BIANNUALLY ||
                            meeting.getRepeatFrequency() == MeetingRepeatFrequency.YEARLY
            ) {
                if (meeting.getMonthlyRepeatMode() == null) {
                    throw new RuntimeException("Monthly repeat mode is required");
                }

                if (
                        meeting.getRepeatFrequency() == MeetingRepeatFrequency.QUARTERLY ||
                                meeting.getRepeatFrequency() == MeetingRepeatFrequency.BIANNUALLY ||
                                meeting.getRepeatFrequency() == MeetingRepeatFrequency.YEARLY
                ) {
                    if (meeting.getRepeatMonths() == null || meeting.getRepeatMonths().isBlank()) {
                        throw new RuntimeException("Select at least one month");
                    }
                }

                if (meeting.getMonthlyRepeatMode() == MeetingMonthlyRepeatMode.ON_DAY) {
                    if (meeting.getRepeatMonthDay() == null ||
                            meeting.getRepeatMonthDay() < 1 ||
                            meeting.getRepeatMonthDay() > 31) {
                        throw new RuntimeException("Repeat month day must be between 1 and 31");
                    }
                }

                if (meeting.getMonthlyRepeatMode() == MeetingMonthlyRepeatMode.ON_THE) {
                    if (meeting.getRepeatWeekOrder() == null || meeting.getRepeatWeekDay() == null) {
                        throw new RuntimeException("Week order and weekday are required");
                    }
                }
            }
        }

        if (meeting.getRepeatEndType() == null) {
            meeting.setRepeatEndType(MeetingRepeatEndType.NEVER);
        }

        if (meeting.getRepeatEndType() == MeetingRepeatEndType.AFTER) {
            if (meeting.getRepeatEndAfterTimes() == null || meeting.getRepeatEndAfterTimes() < 1) {
                throw new RuntimeException("Repeat end after times must be at least 1");
            }
        }

        if (meeting.getRepeatEndType() == MeetingRepeatEndType.ON_DATE) {
            if (meeting.getRepeatEndDate() == null) {
                throw new RuntimeException("Repeat end date is required");
            }
        }
    }

    private MeetingResponse toResponse(MeetingEntity meeting) {
        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .meetingVenue(meeting.getMeetingVenue())
                .location(meeting.getLocation())
                .allDay(meeting.getAllDay())
                .fromDateTime(meeting.getFromDateTime())
                .toDateTime(meeting.getToDateTime())
                .hostName(meeting.getHostName())
                .hostEmail(meeting.getHostEmail())
                .participantType(meeting.getParticipantType())
                .participantIds(meeting.getParticipantIds())
                .participantNames(meeting.getParticipantNames())
                .participantEmails(meeting.getParticipantEmails())
                .showContactsWithoutEmail(meeting.getShowContactsWithoutEmail())
                .showLeadsWithoutEmail(meeting.getShowLeadsWithoutEmail())
                .showUsersWithoutEmail(meeting.getShowUsersWithoutEmail())
                .relatedType(meeting.getRelatedType())
                .relatedId(meeting.getRelatedId())
                .relatedName(meeting.getRelatedName())
                .participantsReminder(meeting.getParticipantsReminder())
                .reminder(meeting.getReminder())
                .secondReminder(meeting.getSecondReminder())
                .repeatEnabled(meeting.getRepeatEnabled())
                .repeatType(meeting.getRepeatType())
                .repeatFrequency(meeting.getRepeatFrequency())
                .repeatEvery(meeting.getRepeatEvery())
                .repeatWeekDays(meeting.getRepeatWeekDays())
                .repeatMonths(meeting.getRepeatMonths())
                .monthlyRepeatMode(meeting.getMonthlyRepeatMode())
                .repeatMonthDay(meeting.getRepeatMonthDay())
                .repeatWeekOrder(meeting.getRepeatWeekOrder())
                .repeatWeekDay(meeting.getRepeatWeekDay())
                .repeatEndType(meeting.getRepeatEndType())
                .repeatEndAfterTimes(meeting.getRepeatEndAfterTimes())
                .repeatEndDate(meeting.getRepeatEndDate())
                .description(meeting.getDescription())
                .companyId(meeting.getCompanyId())
                .createdBy(meeting.getCreatedBy())
                .createdDate(meeting.getCreatedDate())
                .updatedDate(meeting.getUpdatedDate())
                .build();
    }
}