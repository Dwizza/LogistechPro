package com.logistechpro.Repository;

import com.logistechpro.Models.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PORepository extends JpaRepository<PurchaseOrder, Long> {
}
