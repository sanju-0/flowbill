package com.freelancer.tracker.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardDTO {
    private BigDecimal totalEarned;
    private BigDecimal totalPending;
    private BigDecimal totalOverdue;
    private Long activeClients;
    private Long overdueCount;
    private List<InvoiceDTO> recentInvoices;
    private List<ClientDTO> topClients;
}