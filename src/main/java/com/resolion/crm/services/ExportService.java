package com.resolion.crm.services;

import com.resolion.crm.dpo.ReportRunResponse;
import org.springframework.stereotype.Service;

@Service
public class ExportService {

    public byte[] generateCSV(ReportRunResponse data) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.join(",", data.getColumns())).append("\n");

        for (var row : data.getRows()) {
            for (String col : data.getColumns()) {
                sb.append(row.getOrDefault(col, "")).append(",");
            }
            sb.append("\n");
        }

        return sb.toString().getBytes();
    }
}