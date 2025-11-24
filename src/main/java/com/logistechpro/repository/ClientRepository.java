package com.logistechpro.repository;

import com.logistechpro.models.Client;
import com.logistechpro.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUser(User user);
}
