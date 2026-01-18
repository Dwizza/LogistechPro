package com.logistechpro.service;

import com.logistechpro.dto.request.WarehouseManagerCreateRequest;
import com.logistechpro.dto.request.WarehouseManagerUpdateRequest;
import com.logistechpro.dto.response.UserResponse;
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

import java.util.List;
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
    void updateWarehouseManager_success() {
        WarehouseManagerUpdateRequest update = new WarehouseManagerUpdateRequest();
        update.setName("New Name");
        update.setEmail("new@test.com");
        update.setPassword("newpass123");
        update.setActive(false);

        User existing = User.builder()
                .id(10L)
                .name("Old")
                .email("old@test.com")
                .passwordHash("OLDHASH")
                .role(Role.WAREHOUSE_MANAGER)
                .active(true)
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass123")).thenReturn("NEWHASH");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse resp = service.updateWarehouseManager(10L, update);

        assertEquals(10L, resp.getId());
        assertEquals("New Name", resp.getName());
        assertEquals("new@test.com", resp.getEmail());
        assertFalse(resp.isActive());
        verify(userRepository).findById(10L);
        verify(userRepository).findByEmail("new@test.com");
        verify(passwordEncoder).encode("newpass123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateWarehouseManager_userNotFound() {
        WarehouseManagerUpdateRequest update = new WarehouseManagerUpdateRequest();
        update.setName("X");

        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateWarehouseManager(404L, update));
        assertTrue(ex.getMessage().contains("Utilisateur introuvable"));
        verify(userRepository).findById(404L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateWarehouseManager_roleMismatch() {
        WarehouseManagerUpdateRequest update = new WarehouseManagerUpdateRequest();
        update.setName("X");

        User notManager = User.builder()
                .id(2L)
                .name("Client")
                .email("client@test.com")
                .passwordHash("H")
                .role(Role.CLIENT)
                .active(true)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(notManager));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateWarehouseManager(2L, update));
        assertTrue(ex.getMessage().toLowerCase().contains("warehouse manager"));
        verify(userRepository).findById(2L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateWarehouseManager_emailExistsForAnotherUser() {
        WarehouseManagerUpdateRequest update = new WarehouseManagerUpdateRequest();
        update.setEmail("taken@test.com");

        User existing = User.builder()
                .id(10L)
                .name("Old")
                .email("old@test.com")
                .passwordHash("OLDHASH")
                .role(Role.WAREHOUSE_MANAGER)
                .active(true)
                .build();
        User other = User.builder()
                .id(11L)
                .name("Other")
                .email("taken@test.com")
                .passwordHash("HASH")
                .role(Role.WAREHOUSE_MANAGER)
                .active(true)
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(other));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateWarehouseManager(10L, update));
        assertTrue(ex.getMessage().contains("Email already exists"));
        verify(userRepository).findByEmail("taken@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteWarehouseManager_success() {
        User existing = User.builder()
                .id(7L)
                .name("M")
                .email("m@test.com")
                .passwordHash("H")
                .role(Role.WAREHOUSE_MANAGER)
                .active(true)
                .build();

        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));

        service.deleteWarehouseManager(7L);

        verify(userRepository).findById(7L);
        verify(userRepository).delete(existing);
    }

    @Test
    void deleteWarehouseManager_roleMismatch() {
        User notManager = User.builder()
                .id(8L)
                .name("Client")
                .email("c@test.com")
                .passwordHash("H")
                .role(Role.CLIENT)
                .active(true)
                .build();

        when(userRepository.findById(8L)).thenReturn(Optional.of(notManager));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteWarehouseManager(8L));
        assertTrue(ex.getMessage().toLowerCase().contains("warehouse manager"));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteWarehouseManager_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteWarehouseManager(99L));
        assertTrue(ex.getMessage().contains("Utilisateur introuvable"));
        verify(userRepository).findById(99L);
        verify(userRepository, never()).delete(any());
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

    @Test
    void getAllWarehouseManagers_success() {
        User m1 = User.builder().id(1L).name("M1").email("m1@test.com").role(Role.WAREHOUSE_MANAGER).passwordHash("X").active(true).build();
        User m2 = User.builder().id(2L).name("M2").email("m2@test.com").role(Role.WAREHOUSE_MANAGER).passwordHash("Y").active(false).build();
        when(userRepository.findAllByRole(Role.WAREHOUSE_MANAGER)).thenReturn(List.of(m1, m2));

        List<UserResponse> resp = service.getAllWarehouseManagers();

        assertEquals(2, resp.size());
        assertEquals("m1@test.com", resp.get(0).getEmail());
        assertEquals(Role.WAREHOUSE_MANAGER, resp.get(0).getRole());
        assertFalse(resp.get(1).isActive());
        verify(userRepository).findAllByRole(Role.WAREHOUSE_MANAGER);
    }
}
