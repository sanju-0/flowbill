package com.freelancer.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateClientRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String email;
    private String phone;
    private String companyName;
}