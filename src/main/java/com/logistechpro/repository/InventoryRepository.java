package com.logistechpro.repository;

import com.logistechpro.models.Inventory;
import com.logistechpro.models.Product;
import com.logistechpro.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
    List<Inventory> findAllByProduct(Product product);
}
