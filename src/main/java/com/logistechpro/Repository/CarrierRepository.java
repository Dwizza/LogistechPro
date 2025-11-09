package com.logistechpro.Repository;

import com.logistechpro.Models.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Optional<Carrier> findByContactEmail(String email);
}
