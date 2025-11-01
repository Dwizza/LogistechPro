package com.logistechpro.Repository;

import com.logistechpro.Models.Client;
import com.logistechpro.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUser(User user);
}
