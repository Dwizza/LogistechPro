package com.logistechpro.config;

import com.logistechpro.Models.Enums.Role;
import com.logistechpro.Models.User;
import com.logistechpro.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin.email}")
    private String adminEmail;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.admin.name}")
    private String adminName;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findByEmail(adminEmail).ifPresentOrElse(existing -> {
            log.info("Admin déjà présent (email={}), aucune création.", adminEmail);
        }, () -> {
            User admin = User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin initial créé: {} ({})", adminName, adminEmail);
        });
    }
}

