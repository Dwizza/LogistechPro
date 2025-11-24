package com.logistechpro.repository;

import com.logistechpro.models.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Optional<Carrier> findByContactEmail(String email);
}
