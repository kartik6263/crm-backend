package com.resolion.crm.dto;

import java.util.List;

public class AiInsightResponse {
    private List<String> insights;

    public AiInsightResponse(List<String> insights) {
        this.insights = insights;
    }

    public List<String> getInsights() { return insights; }
}