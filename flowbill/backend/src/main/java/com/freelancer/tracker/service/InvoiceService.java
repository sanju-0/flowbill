package com.freelancer.tracker.service;

import com.freelancer.tracker.dto.*;
import com.freelancer.tracker.entity.*;
import com.freelancer.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProjectRepository projectRepository;

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
        return toDTO(invoice);
    }

    public List<InvoiceDTO> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDate.now())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InvoiceDTO createInvoice(CreateInvoiceRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found: " + request.getProjectId()));

        Invoice invoice = Invoice.builder()
                .project(project)
                .invoiceNumber(request.getInvoiceNumber())
                .issueDate(request.getIssueDate())
                .dueDate(request.getDueDate())
                .amount(request.getAmount())
                .status("UNPAID")
                .build();

        return toDTO(invoiceRepository.save(invoice));
    }

    public InvoiceDTO updateStatus(Long id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
        invoice.setStatus(status);
        return toDTO(invoiceRepository.save(invoice));
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    public InvoiceDTO toDTO(Invoice invoice) {
        String clientName = "";
        String clientEmail = "";
        String projectTitle = "";

        if (invoice.getProject() != null) {
            projectTitle = invoice.getProject().getTitle();
            if (invoice.getProject().getClient() != null) {
                clientName = invoice.getProject().getClient().getName();
                clientEmail = invoice.getProject().getClient().getEmail() != null
                        ? invoice.getProject().getClient().getEmail()
                        : "";
            }
        }

        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .clientName(clientName)
                .clientEmail(clientEmail)
                .projectTitle(projectTitle)
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .amount(invoice.getAmount())
                .status(invoice.getStatus())
                .build();
    }

    public List<InvoiceDTO> getInvoicesByClientId(Long clientId) {
        return invoiceRepository.findByClientId(clientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}