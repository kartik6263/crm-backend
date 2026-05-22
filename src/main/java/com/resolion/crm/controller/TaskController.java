package com.resolion.crm.controller;


import com.resolion.crm.ENUMS.TaskStatus;
import com.resolion.crm.dpo.TaskRequest;
import com.resolion.crm.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestParam String email,
                                        @RequestParam Long companyId,
                                        @RequestBody TaskRequest request) {
        try {
            return ResponseEntity.ok(taskService.createTask(email, companyId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<?> getVisibleTasks(@RequestParam String email,
                                             @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(taskService.getVisibleTasks(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@RequestParam String email,
                                         @PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskById(email, id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@RequestParam String email,
                                        @PathVariable Long id,
                                        @RequestBody TaskRequest request) {
        try {
            return ResponseEntity.ok(taskService.updateTask(email, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@RequestParam String email,
                                              @PathVariable Long id,
                                              @RequestParam TaskStatus status) {
        try {
            return ResponseEntity.ok(taskService.updateTaskStatus(email, id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@RequestParam String email,
                                        @PathVariable Long id) {
        try {
            taskService.deleteTask(email, id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<?> getTasksByLead(@RequestParam String email,
                                            @RequestParam Long companyId,
                                            @PathVariable Long leadId) {
        try {
            return ResponseEntity.ok(taskService.getTasksByLead(email, companyId, leadId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getTasksByStatus(@RequestParam String email,
                                              @RequestParam Long companyId,
                                              @RequestParam TaskStatus status) {
        try {
            return ResponseEntity.ok(taskService.getTasksByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/due-date")
    public ResponseEntity<?> getTasksByDueDate(@RequestParam String email,
                                               @RequestParam Long companyId,
                                               @RequestParam LocalDate dueDate) {
        try {
            return ResponseEntity.ok(taskService.getTasksByDueDate(email, companyId, dueDate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countTasks(@RequestParam String email,
                                        @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(taskService.countTasks(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count/status")
    public ResponseEntity<?> countTasksByStatus(@RequestParam String email,
                                                @RequestParam Long companyId,
                                                @RequestParam TaskStatus status) {
        try {
            return ResponseEntity.ok(taskService.countTasksByStatus(email, companyId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/count/today")
    public ResponseEntity<?> countTodayTasks(@RequestParam String email,
                                             @RequestParam Long companyId) {
        try {
            return ResponseEntity.ok(taskService.countTodayTasks(email, companyId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}