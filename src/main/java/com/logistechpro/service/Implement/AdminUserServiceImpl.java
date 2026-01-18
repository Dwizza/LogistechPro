package com.logistechpro.service.Implement;

import com.logistechpro.dto.request.WarehouseManagerCreateRequest;
import com.logistechpro.dto.request.WarehouseManagerUpdateRequest;
import com.logistechpro.dto.response.UserResponse;
import com.logistechpro.models.Enums.Role;
import com.logistechpro.models.User;
import com.logistechpro.repository.UserRepository;
import com.logistechpro.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createWarehouseManager(WarehouseManagerCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        User manager = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.WAREHOUSE_MANAGER)
                .active(true)
                .build();
        User saved = userRepository.save(manager);
        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .active(saved.isActive())
                .build();
    }

    @Override
    public UserResponse updateWarehouseManager(Long id, WarehouseManagerUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (existing.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new RuntimeException("Ce user n'est pas un warehouse manager");
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(existing.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .filter(u -> !u.getId().equals(existing.getId()))
                    .ifPresent(u -> {
                        throw new RuntimeException("Email already exists!");
                    });
            existing.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            existing.setName(request.getName());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }

        User saved = userRepository.save(existing);
        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .active(saved.isActive())
                .build();
    }

    @Override
    public void deleteWarehouseManager(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (existing.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new RuntimeException("Ce user n'est pas un warehouse manager");
        }

        userRepository.delete(existing);
    }

    @Override
    public UserResponse getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Non authentifié");
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Accès refusé: rôle ADMIN requis");
        }
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    @Override
    public List<UserResponse> getAllWarehouseManagers() {
        return userRepository.findAllByRole(Role.WAREHOUSE_MANAGER)
                .stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .active(u.isActive())
                        .build())
                .toList();
    }
}
