package com.freelancer.tracker.repository;

import com.freelancer.tracker.entity.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {
    List<ReminderLog> findByInvoiceId(Long invoiceId);
}