package com.freelancer.tracker.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateProjectRequest {
    private Long clientId;
    private String title;
    private String description;
    private BigDecimal totalAmount;
}