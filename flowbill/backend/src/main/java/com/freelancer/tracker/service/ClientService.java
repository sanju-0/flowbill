package com.freelancer.tracker.service;

import com.freelancer.tracker.dto.*;
import com.freelancer.tracker.entity.Client;
import com.freelancer.tracker.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        return toDTO(client);
    }

    public ClientDTO createClient(CreateClientRequest request) {
        Client client = Client.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .companyName(request.getCompanyName())
                .build();
        return toDTO(clientRepository.save(client));
    }

    public ClientDTO updateClient(Long id, CreateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setCompanyName(request.getCompanyName());
        return toDTO(clientRepository.save(client));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public ClientDTO toDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .companyName(client.getCompanyName())
                .build();
    }
}