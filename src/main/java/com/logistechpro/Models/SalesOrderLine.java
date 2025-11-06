package com.logistechpro.Models;

import com.logistechpro.Models.Product;
import com.logistechpro.Models.SalesOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Product product;

    @Positive
    private Integer quantity;

    @Positive
    private BigDecimal unitPrice;

    private Integer qtyReserved = 0;
    private Integer qtyShortage = 0;
    private boolean backOrder = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;
}
