package com.freelancer.tracker.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private String clientName;
    private String clientEmail;
    private String projectTitle;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private String status;
}

