package com.logistechpro.Models;

import com.logistechpro.Models.Enums.CarrierStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String contactEmail;

    @Positive
    private BigDecimal capacityWeight;

    @Positive
    private BigDecimal capacityVolume;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private CarrierStatus status;
}

