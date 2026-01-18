package com.logistechpro.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Champs optionnels: si null => مايتبدل والو.
 */
@Data
public class WarehouseManagerUpdateRequest {

    @Size(max = 100)
    private String name;

    @Email
    @Size(max = 255)
    private String email;

    @Size(min = 6, max = 255)
    private String password;

    private Boolean active;
}
