package com.freelancer.tracker.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentDTO {
    private Long id;
    private Long invoiceId;
    private String invoiceNumber;
    private String clientName;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private String paymentMode;
    private String notes;
}