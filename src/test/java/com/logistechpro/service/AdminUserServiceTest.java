package com.logistechpro.service;

import com.logistechpro.dto.Request.WarehouseManagerCreateRequest;
import com.logistechpro.dto.Response.UserResponse;
import com.logistechpro.models.Enums.Role;
import com.logistechpro.models.User;
import com.logistechpro.repository.UserRepository;
import com.logistechpro.service.Implement.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    AdminUserServiceImpl service;

    WarehouseManagerCreateRequest request;

    @BeforeEach
    void setup() {
        request = new WarehouseManagerCreateRequest();
        request.setName("Manager");
        request.setEmail("manager@test.com");
        request.setPassword("secret123");
        SecurityContextHolder.clearContext();
    }

    @Test
    void createWarehouseManager_success() {
        when(userRepository.findByEmail("manager@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("ENC");
        User saved = User.builder().id(5L).name("Manager").email("manager@test.com").passwordHash("ENC").role(Role.WAREHOUSE_MANAGER).active(true).build();
        when(userRepository.save(any(User.class))).thenReturn(saved);
        UserResponse resp = service.createWarehouseManager(request);
        assertEquals(5L, resp.getId());
        assertEquals(Role.WAREHOUSE_MANAGER, resp.getRole());
        verify(userRepository).findByEmail("manager@test.com");
        verify(passwordEncoder).encode("secret123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createWarehouseManager_emailExists() {
        when(userRepository.findByEmail("manager@test.com")).thenReturn(Optional.of(new User()));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createWarehouseManager(request));
        assertTrue(ex.getMessage().contains("Email already exists"));
        verify(userRepository).findByEmail("manager@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void getCurrentAdmin_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("admin@test.com");
        SecurityContext sc = new SecurityContext() {
            private Authentication a = auth;
            @Override public Authentication getAuthentication() { return a; }
            @Override public void setAuthentication(Authentication authentication) { this.a = authentication; }
        };
        SecurityContextHolder.setContext(sc);
        User admin = User.builder().id(9L).name("Admin").email("admin@test.com").role(Role.ADMIN).passwordHash("X").active(true).build();
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        UserResponse resp = service.getCurrentAdmin();
        assertEquals(9L, resp.getId());
        assertEquals(Role.ADMIN, resp.getRole());
    }

    @Test
    void getCurrentAdmin_noAuth() {
        SecurityContextHolder.clearContext();
        assertThrows(RuntimeException.class, () -> service.getCurrentAdmin());
    }

    @Test
    void getCurrentAdmin_userNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("missing@test.com");
        SecurityContext sc = new SecurityContext() {
            private Authentication a = auth;
            @Override public Authentication getAuthentication() { return a; }
            @Override public void setAuthentication(Authentication authentication) { this.a = authentication; }
        };
        SecurityContextHolder.setContext(sc);
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getCurrentAdmin());
        assertTrue(ex.getMessage().contains("Utilisateur introuvable"));
    }

    @Test
    void getCurrentAdmin_notAdminRole() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("manager@test.com");
        SecurityContext sc = new SecurityContext() {
            private Authentication a = auth;
            @Override public Authentication getAuthentication() { return a; }
            @Override public void setAuthentication(Authentication authentication) { this.a = authentication; }
        };
        SecurityContextHolder.setContext(sc);
        User mgr = User.builder().id(3L).name("Manager").email("manager@test.com").role(Role.WAREHOUSE_MANAGER).passwordHash("X").active(true).build();
        when(userRepository.findByEmail("manager@test.com")).thenReturn(Optional.of(mgr));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getCurrentAdmin());
        assertTrue(ex.getMessage().contains("Accès refusé"));
    }
}

