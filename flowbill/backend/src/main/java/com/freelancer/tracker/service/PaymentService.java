package com.freelancer.tracker.service;

import com.freelancer.tracker.dto.*;
import com.freelancer.tracker.entity.*;
import com.freelancer.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO recordPayment(CreatePaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + request.getInvoiceId()));

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amountPaid(request.getAmountPaid())
                .paymentDate(request.getPaymentDate())
                .paymentMode(request.getPaymentMode())
                .notes(request.getNotes())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Auto update invoice status
        updateInvoiceStatusAfterPayment(invoice);

        return toDTO(saved);
    }

    private void updateInvoiceStatusAfterPayment(Invoice invoice) {
        java.math.BigDecimal totalPaid = paymentRepository
                .getTotalPaidForInvoice(invoice.getId());

        if (totalPaid == null) return;

        if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIAL");
        }
        invoiceRepository.save(invoice);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public PaymentDTO toDTO(Payment payment) {
        String clientName = "";
        String invoiceNumber = "";

        if (payment.getInvoice() != null) {
            invoiceNumber = payment.getInvoice().getInvoiceNumber();
            if (payment.getInvoice().getProject() != null &&
                payment.getInvoice().getProject().getClient() != null) {
                clientName = payment.getInvoice().getProject().getClient().getName();
            }
        }

        return PaymentDTO.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoice().getId())
                .invoiceNumber(invoiceNumber)
                .clientName(clientName)
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .paymentMode(payment.getPaymentMode())
                .notes(payment.getNotes())
                .build();
    }
}