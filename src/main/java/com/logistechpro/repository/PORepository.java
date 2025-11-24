package com.logistechpro.repository;

import com.logistechpro.models.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PORepository extends JpaRepository<PurchaseOrder, Long> {
}
