package com.resolion.crm.entity;

public class HomeDashboardStats {
    private long tasksDueToday;
    private long newLeads;
    private long openDeals;
    private long meetingsToday;
    private long pendingReminders;
    private long activitiesToday;

    public HomeDashboardStats(long tasksDueToday, long newLeads, long openDeals,
                              long meetingsToday, long pendingReminders, long activitiesToday) {
        this.tasksDueToday = tasksDueToday;
        this.newLeads = newLeads;
        this.openDeals = openDeals;
        this.meetingsToday = meetingsToday;
        this.pendingReminders = pendingReminders;
        this.activitiesToday = activitiesToday;
    }

    public long getTasksDueToday() { return tasksDueToday; }
    public long getNewLeads() { return newLeads; }
    public long getOpenDeals() { return openDeals; }
    public long getMeetingsToday() { return meetingsToday; }
    public long getPendingReminders() { return pendingReminders; }
    public long getActivitiesToday() { return activitiesToday; }
}