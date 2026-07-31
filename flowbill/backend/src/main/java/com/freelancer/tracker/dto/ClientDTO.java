package com.freelancer.tracker.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String companyName;
}