package com.resolion.crm.dpo;

import java.util.List;

public class ChartResponse {
    private List<String> labels;
    private List<Long> values;

    public ChartResponse(List<String> labels, List<Long> values) {
        this.labels = labels;
        this.values = values;
    }

    public List<String> getLabels() { return labels; }
    public List<Long> getValues() { return values; }
}