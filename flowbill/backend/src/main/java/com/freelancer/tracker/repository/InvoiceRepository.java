package com.freelancer.tracker.repository;

import com.freelancer.tracker.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByStatus(String status);

    List<Invoice> findByProjectClientId(Long clientId);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :today AND i.status != 'PAID'")
    List<Invoice> findOverdueInvoices(LocalDate today);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate = :date AND i.status != 'PAID'")
    List<Invoice> findInvoicesDueOn(LocalDate date);

    @Query("SELECT i FROM Invoice i WHERE i.project.client.id = :clientId")
    List<Invoice> findByClientId(Long clientId);
}