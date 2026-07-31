package com.freelancer.tracker.service;

import com.freelancer.tracker.dto.*;
import com.freelancer.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;
    private final ClientService clientService;

    public DashboardDTO getSummary() {

        // Total earned — sum of all payments
        BigDecimal totalEarned = paymentRepository.findAll()
                .stream()
                .map(p -> p.getAmountPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total pending — UNPAID invoices
        BigDecimal totalPending = invoiceRepository.findByStatus("UNPAID")
                .stream()
                .map(i -> i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total overdue
        BigDecimal totalOverdue = invoiceRepository
                .findOverdueInvoices(LocalDate.now())
                .stream()
                .map(i -> i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Overdue count
        Long overdueCount = (long) invoiceRepository
                .findOverdueInvoices(LocalDate.now()).size();

        // Active clients
        Long activeClients = clientRepository.count();

        // Recent 5 invoices
        List<InvoiceDTO> recentInvoices = invoiceRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(invoiceService::toDTO)
                .collect(Collectors.toList());

        // Top 3 clients
        List<ClientDTO> topClients = clientRepository.findAll()
                .stream()
                .limit(3)
                .map(clientService::toDTO)
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalEarned(totalEarned)
                .totalPending(totalPending)
                .totalOverdue(totalOverdue)
                .activeClients(activeClients)
                .overdueCount(overdueCount)
                .recentInvoices(recentInvoices)
                .topClients(topClients)
                .build();
    }
}