package com.leadmatrix.crm.Scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderSchedular {

    @Scheduled(cron="0 0 9 * * ?")

    public void checkReminders(){

        System.out.println("Checking today's reminders");

    }

}
