package com.logistechpro.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistechpro.AbstractIntegrationTest;
import com.logistechpro.dto.request.LoginRequest;
import com.logistechpro.dto.request.WarehouseManagerUpdateRequest;
import com.logistechpro.models.Enums.Role;
import com.logistechpro.models.User;
import com.logistechpro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AdminUserControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long manager1Id;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setName("Admin");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User manager1 = new User();
        manager1.setEmail("manager1@test.com");
        manager1.setPasswordHash(passwordEncoder.encode("manager123"));
        manager1.setName("Manager 1");
        manager1.setRole(Role.WAREHOUSE_MANAGER);
        manager1.setActive(true);
        manager1Id = userRepository.save(manager1).getId();

        User manager2 = new User();
        manager2.setEmail("manager2@test.com");
        manager2.setPasswordHash(passwordEncoder.encode("manager123"));
        manager2.setName("Manager 2");
        manager2.setRole(Role.WAREHOUSE_MANAGER);
        manager2.setActive(false);
        userRepository.save(manager2);

        User client = new User();
        client.setEmail("client@test.com");
        client.setPasswordHash(passwordEncoder.encode("client123"));
        client.setName("Client");
        client.setRole(Role.CLIENT);
        userRepository.save(client);
    }

    private String loginAsAdminAndGetAccessToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@test.com");
        req.setPassword("admin123");

        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        return root.get("accessToken").asText();
    }

    @Test
    void getAllWarehouseManagers_shouldReturnManagers_onlyForAdmin() throws Exception {
        String token = loginAsAdminAndGetAccessToken();

        mockMvc.perform(get("/api/admin/users/warehouse-managers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].role").value("WAREHOUSE_MANAGER"))
                .andExpect(jsonPath("$[1].role").value("WAREHOUSE_MANAGER"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateWarehouseManager_shouldUpdateFields() throws Exception {
        String token = loginAsAdminAndGetAccessToken();

        WarehouseManagerUpdateRequest update = new WarehouseManagerUpdateRequest();
        update.setName("Manager 1 Updated");
        update.setActive(false);

        mockMvc.perform(put("/api/admin/users/warehouse-managers/{id}", manager1Id)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(manager1Id))
                .andExpect(jsonPath("$.name").value("Manager 1 Updated"))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.role").value("WAREHOUSE_MANAGER"));
    }

    @Test
    void deleteWarehouseManager_shouldReturnNoContent() throws Exception {
        String token = loginAsAdminAndGetAccessToken();

        mockMvc.perform(delete("/api/admin/users/warehouse-managers/{id}", manager1Id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // verify it's deleted
        assertFalse(userRepository.findById(manager1Id).isPresent());
    }
}
