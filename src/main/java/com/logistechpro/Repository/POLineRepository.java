package com.logistechpro.Repository;

import com.logistechpro.Models.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface POLineRepository extends JpaRepository<PurchaseOrderLine, Long> {
}
