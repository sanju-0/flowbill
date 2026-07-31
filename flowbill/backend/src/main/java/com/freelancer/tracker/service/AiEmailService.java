package com.freelancer.tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class AiEmailService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GROQ_URL =
        "https://api.groq.com/openai/v1/chat/completions";

    public String generateReminderEmail(String clientName,
                                         double amount,
                                         long daysOverdue,
                                         String tone) {

        String prompt = buildPrompt(clientName, amount, daysOverdue, tone);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
            "model", "llama-3.3-70b-versatile",
            "max_tokens", 500,
            "messages", List.of(
                Map.of(
                    "role", "user",
                    "content", prompt
                )
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                GROQ_URL, request, Map.class
            );

            Map responseBody = response.getBody();
            if (responseBody != null) {
                List<Map> choices = (List<Map>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map message = (Map) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            return "Error generating email: " + e.getMessage();
        }

        return "Could not generate email.";
    }

    private String buildPrompt(String clientName,
                                double amount,
                                long daysOverdue,
                                String tone) {
        return String.format("""
            You are a professional freelancer assistant.
            Write a payment reminder email with the following details:
            
            - Client Name: %s
            - Amount Due: ₹%.0f
            - Days Overdue: %d days
            - Tone: %s
            
            Rules:
            - Keep it under 150 words
            - Be professional and clear
            - Include subject line at top like "Subject: ..."
            - Do not include sender name placeholders
            - End with a polite call to action
            
            Write only the email, nothing else.
            """, clientName, amount, daysOverdue, tone);
    }
}