package com.freelancer.tracker.controller;

import com.freelancer.tracker.service.AiEmailService;
import com.freelancer.tracker.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AiController {

    private final AiEmailService aiEmailService;
    private final EmailService emailService;

    @PostMapping("/generate-reminder")
    public ResponseEntity<Map<String, String>> generateReminder(
            @RequestBody Map<String, Object> request) {

        String clientName = (String) request.get("clientName");
        double amount = Double.parseDouble(request.get("amount").toString());
        long daysOverdue = Long.parseLong(request.get("daysOverdue").toString());
        String tone = (String) request.getOrDefault("tone", "polite");

        String email = aiEmailService.generateReminderEmail(
                clientName, amount, daysOverdue, tone);

        return ResponseEntity.ok(Map.of("email", email));
    }

    @PostMapping("/send-reminder")
    public ResponseEntity<Map<String, String>> sendReminder(
            @RequestBody Map<String, Object> request) {

        String clientName = (String) request.get("clientName");
        String clientEmail = (String) request.get("clientEmail");
        double amount = Double.parseDouble(request.get("amount").toString());
        long daysOverdue = Long.parseLong(request.get("daysOverdue").toString());
        String tone = (String) request.getOrDefault("tone", "polite");

        // AI email generate karo
        String emailBody = aiEmailService.generateReminderEmail(
                clientName, amount, daysOverdue, tone);

        // Subject extract karo
        String subject = "Payment Reminder";
        String body = emailBody;

        if (emailBody.startsWith("Subject:")) {
            String[] lines = emailBody.split("\n", 3);
            subject = lines[0].replace("Subject:", "").trim();
            body = lines.length > 2 ? lines[2].trim() : emailBody;
        }

        // Email bhejo
        emailService.sendEmail(clientEmail, subject, body);

        return ResponseEntity.ok(Map.of(
                "email", emailBody,
                "message", "Email sent successfully to " + clientEmail));
    }
}