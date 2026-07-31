package com.freelancer.tracker.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectDTO {
    private Long id;
    private Long clientId;
    private String clientName;
    private String title;
    private String description;
    private BigDecimal totalAmount;
    private String status;
}