package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Request.WarehouseManagerCreateRequest;
import com.logistechpro.DTO.Response.UserResponse;
import com.logistechpro.Models.Enums.Role;
import com.logistechpro.Models.User;
import com.logistechpro.Repository.UserRepository;
import com.logistechpro.Service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

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
}
