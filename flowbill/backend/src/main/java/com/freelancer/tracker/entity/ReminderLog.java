package com.freelancer.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminder_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReminderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    private LocalDateTime sentAt;

    @Column(length = 20)
    private String reminderType = "EMAIL";

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
    }
}