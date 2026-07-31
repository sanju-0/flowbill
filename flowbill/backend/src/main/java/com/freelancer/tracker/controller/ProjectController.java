package com.freelancer.tracker.controller;

import com.freelancer.tracker.dto.CreateProjectRequest;
import com.freelancer.tracker.dto.ProjectDTO;
import com.freelancer.tracker.entity.Client;
import com.freelancer.tracker.entity.Project;
import com.freelancer.tracker.repository.ClientRepository;
import com.freelancer.tracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(
            projectRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByClient(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(
            projectRepository.findByClientId(clientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(
            @RequestBody CreateProjectRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Project project = Project.builder()
                .client(client)
                .title(request.getTitle())
                .description(request.getDescription())
                .totalAmount(request.getTotalAmount())
                .status("ACTIVE")
                .build();

        return ResponseEntity.ok(toDTO(projectRepository.save(project)));
    }

    private ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .clientId(project.getClient() != null ? project.getClient().getId() : null)
                .clientName(project.getClient() != null ? project.getClient().getName() : "")
                .title(project.getTitle())
                .description(project.getDescription())
                .totalAmount(project.getTotalAmount())
                .status(project.getStatus())
                .build();
    }
}