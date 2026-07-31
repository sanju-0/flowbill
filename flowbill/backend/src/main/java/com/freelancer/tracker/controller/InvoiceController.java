package com.freelancer.tracker.controller;

import com.freelancer.tracker.dto.*;
import com.freelancer.tracker.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices(
            @RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
        }
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceDTO>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InvoiceDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(invoiceService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByClient(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByClientId(clientId));
    }
}