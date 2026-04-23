package com.resolion.crm.controller;

import com.resolion.crm.entity.CalendarEvent;
import com.resolion.crm.respository.CalendarEventRepository;
import com.resolion.crm.services.CompanyAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Autowired
    private CompanyAccessService companyAccessService;

    private void checkAccess(String email, Long companyId) {
        if (!companyAccessService.hasCompanyAccess(email, companyId)) {
            throw new RuntimeException("Access Denied");
        }
    }

    @GetMapping("/events")
    public List<CalendarEvent> getEvents(@RequestParam String email,
                                         @RequestParam Long companyId) {
        checkAccess(email, companyId);
        return calendarEventRepository.findByCompanyIdOrderByEventDateDesc(companyId);
    }

    @GetMapping("/events/date")
    public List<CalendarEvent> getEventsByDate(@RequestParam String email,
                                               @RequestParam Long companyId,
                                               @RequestParam String date) {
        checkAccess(email, companyId);
        return calendarEventRepository.findByCompanyIdAndEventDateOrderByEventTimeAsc(companyId, date);
    }

    @PostMapping("/events")
    public CalendarEvent createEvent(@RequestParam String email,
                                     @RequestParam Long companyId,
                                     @RequestBody CalendarEvent event) {
        checkAccess(email, companyId);

        event.setCompanyId(companyId);
        event.setUserEmail(email);
        event.setCreatedDate(LocalDateTime.now().toString());

        return calendarEventRepository.save(event);
    }

    @PutMapping("/events/{id}")
    public CalendarEvent updateEvent(@RequestParam String email,
                                     @RequestParam Long companyId,
                                     @PathVariable Long id,
                                     @RequestBody CalendarEvent newEvent) {
        checkAccess(email, companyId);

        CalendarEvent old = calendarEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!old.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        old.setTitle(newEvent.getTitle());
        old.setDescription(newEvent.getDescription());
        old.setEventDate(newEvent.getEventDate());
        old.setEventTime(newEvent.getEventTime());
        old.setAmPm(newEvent.getAmPm());

        return calendarEventRepository.save(old);
    }

    @DeleteMapping("/events/{id}")
    public String deleteEvent(@RequestParam String email,
                              @RequestParam Long companyId,
                              @PathVariable Long id) {
        checkAccess(email, companyId);

        CalendarEvent event = calendarEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Access Denied");
        }

        calendarEventRepository.delete(event);
        return "Event deleted";
    }
}