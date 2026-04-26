package com.resolion.crm.services;

import com.resolion.crm.dpo.ReportRunResponse;
import com.resolion.crm.entity.ReportSchedule;
import com.resolion.crm.respository.ReportScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportSchedulerService {

    @Autowired
    private ReportScheduleRepository scheduleRepo;

    @Autowired
    private ReportRunService reportRunService;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000) // every 1 min
    public void runScheduledReports() {

        List<ReportSchedule> schedules = scheduleRepo.findByActiveTrue();

        String now = LocalDateTime.now().toString();

        for (ReportSchedule s : schedules) {

            if (s.getNextRunTime().compareTo(now) <= 0) {

                ReportRunResponse data =
                        reportRunService.runReport(s.getReportId(), s.getCompanyId());

                emailService.sendReport(s.getEmailTo(), data);

                // next schedule logic
                s.setNextRunTime(LocalDateTime.now().plusDays(1).toString());

                scheduleRepo.save(s);
            }
        }
    }
}