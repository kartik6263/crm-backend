package com.resolion.crm.services;

import org.springframework.stereotype.Service;

@Service
public class PlanService {

    public int getPlanAmount(String plan) {
        return switch (plan.toUpperCase()) {
            case "PRO" -> 99900;        // ₹999
            case "ENTERPRISE" -> 299900; // ₹2999
            default -> 0;
        };
    }

    public int getDurationDays(String plan) {
        return switch (plan.toUpperCase()) {
            case "PRO" -> 30;
            case "ENTERPRISE" -> 30;
            default -> 0;
        };
    }
}