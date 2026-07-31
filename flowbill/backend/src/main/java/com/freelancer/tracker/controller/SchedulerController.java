package com.freelancer.tracker.controller;

import com.freelancer.tracker.scheduler.ReminderScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SchedulerController {

    private final ReminderScheduler reminderScheduler;

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> triggerManually() {
        reminderScheduler.sendOverdueReminders();
        return ResponseEntity.ok(Map.of(
            "message", "Scheduler triggered successfully"
        ));
    }
}