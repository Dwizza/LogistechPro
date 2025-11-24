package com.logistechpro.repository;

import com.logistechpro.models.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovmentRepository extends JpaRepository<InventoryMovement, Long> {

}
