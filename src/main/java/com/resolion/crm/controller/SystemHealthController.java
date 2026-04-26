package com.resolion.crm.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemHealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "Resolion CRM",
                "time", LocalDateTime.now().toString()
        );
    }
}