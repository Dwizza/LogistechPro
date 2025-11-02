package com.logistechpro.Repository;

import com.logistechpro.Models.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovmentRepository extends JpaRepository<InventoryMovement, Long> {

}
