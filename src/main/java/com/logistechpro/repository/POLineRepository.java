package com.logistechpro.repository;

import com.logistechpro.models.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface POLineRepository extends JpaRepository<PurchaseOrderLine, Long> {
}
