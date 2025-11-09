package com.logistechpro.DTO.Request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CarrierRequest {

        @NotBlank(message = "Le nom est requis")
        @Size(max = 30, message = "Le nom ne doit pas dépasser 30 caractères")
        private String name;

        @NotBlank(message = "Le numéro de contact est requis")
        private String contactEmail;

        @NotBlank(message = "La capacité de poids est requise")
        private String capacityWeight;

        @NotBlank(message = "La capacité de volume est requise")
        private String capacityVolume;

        @NotBlank(message = "Le statut est requis")
        private String status;
}
