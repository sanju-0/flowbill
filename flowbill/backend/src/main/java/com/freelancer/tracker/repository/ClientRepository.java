package com.freelancer.tracker.repository;

import com.freelancer.tracker.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByFreelancerId(Long freelancerId);
    List<Client> findByNameContainingIgnoreCase(String name);
}