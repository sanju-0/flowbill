package com.freelancer.tracker.repository;

import com.freelancer.tracker.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceId(Long invoiceId);

    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.invoice.id = :invoiceId")
    java.math.BigDecimal getTotalPaidForInvoice(Long invoiceId);
}