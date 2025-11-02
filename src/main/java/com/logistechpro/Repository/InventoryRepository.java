package com.logistechpro.Repository;

import com.logistechpro.Models.Inventory;
import com.logistechpro.Models.Product;
import com.logistechpro.Models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
