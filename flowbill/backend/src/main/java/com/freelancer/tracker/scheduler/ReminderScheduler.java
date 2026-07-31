package com.freelancer.tracker.scheduler;

import com.freelancer.tracker.entity.Invoice;
import com.freelancer.tracker.entity.ReminderLog;
import com.freelancer.tracker.repository.InvoiceRepository;
import com.freelancer.tracker.repository.ReminderLogRepository;
import com.freelancer.tracker.service.AiEmailService;
import com.freelancer.tracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final InvoiceRepository invoiceRepository;
    private final ReminderLogRepository reminderLogRepository;
    private final AiEmailService aiEmailService;
    private final EmailService emailService;

    // Har din subah 9 baje
    @Scheduled(cron = "0 0 9 * * *")
    public void sendOverdueReminders() {
        System.out.println("⏰ Scheduler running at: " + LocalDateTime.now());

        List<Invoice> overdueInvoices = invoiceRepository
                .findOverdueInvoices(LocalDate.now());

        System.out.println("Found " + overdueInvoices.size() + " overdue invoices");

        for (Invoice invoice : overdueInvoices) {
            try {
                processOverdueInvoice(invoice);
            } catch (Exception e) {
                System.err.println("Failed for invoice: " +
                    invoice.getInvoiceNumber() + " — " + e.getMessage());
            }
        }
    }

    private void processOverdueInvoice(Invoice invoice) {
        String clientName = "";
        String clientEmail = "";

        if (invoice.getProject() != null &&
            invoice.getProject().getClient() != null) {
            clientName = invoice.getProject().getClient().getName();
            clientEmail = invoice.getProject().getClient().getEmail();
        }

        if (clientEmail == null || clientEmail.isEmpty()) {
            System.out.println("No email for: " + clientName + " — skipping");
            return;
        }

        // Days overdue calculate karo
        long daysOverdue = ChronoUnit.DAYS.between(
            invoice.getDueDate(), LocalDate.now()
        );

        // Tone decide karo based on days overdue
        String tone = daysOverdue <= 7 ? "polite" :
                      daysOverdue <= 15 ? "firm" : "final warning";

        // AI se email generate karo
        String emailBody = aiEmailService.generateReminderEmail(
            clientName,
            invoice.getAmount().doubleValue(),
            daysOverdue,
            tone
        );

        // Subject extract karo
        String subject = "Payment Reminder — " + invoice.getInvoiceNumber();
        String body = emailBody;

        if (emailBody.startsWith("Subject:")) {
            String[] lines = emailBody.split("\n", 3);
            subject = lines[0].replace("Subject:", "").trim();
            body = lines.length > 2 ? lines[2].trim() : emailBody;
        }

        // Email bhejo
        emailService.sendEmail(clientEmail, subject, body);

        // Log save karo
        ReminderLog log = ReminderLog.builder()
                .invoice(invoice)
                .sentAt(LocalDateTime.now())
                .reminderType("EMAIL")
                .build();
        reminderLogRepository.save(log);

        // Invoice status update karo
        invoice.setStatus("OVERDUE");
        invoiceRepository.save(invoice);

        System.out.println("✅ Reminder sent to: " + clientEmail +
            " for invoice: " + invoice.getInvoiceNumber() +
            " (tone: " + tone + ")");
    }
}