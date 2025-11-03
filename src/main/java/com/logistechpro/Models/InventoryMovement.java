package com.logistechpro.Models;

import com.logistechpro.Models.Enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MovementType type;

    @Positive
    @Column(name = "qty")
    private Integer qty;


    @NotNull
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();

    @Size(max = 255)
    private String referenceDocument;

    @ManyToOne(optional = false)
    private Product product;

    @ManyToOne(optional = false)
    private Warehouse warehouse;

    private String description;
}

