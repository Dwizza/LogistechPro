package com.logistechpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import com.logistechpro.repository.UserRepository;
import com.logistechpro.models.User;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final String warehouseManager = "WAREHOUSE_MANAGER";
    private static final String admin = "ADMIN";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User domainUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            if (!domainUser.isActive()) {
                throw new UsernameNotFoundException("Invalid credentials: User is disabled.");
            }
            return org.springframework.security.core.userdetails.User.withUsername(domainUser.getEmail())
                    .password(domainUser.getPasswordHash())
                    .roles(domainUser.getRole().name())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole(admin)
                                .requestMatchers("/api/products/**").hasRole(admin)
                                .requestMatchers("/api/suppliers/**").hasRole(admin)
                                .requestMatchers("/api/warehouses/**").hasRole(admin)
                                .requestMatchers("/api/purchase-orders/**").hasAnyRole(admin,warehouseManager)
                                .requestMatchers("/api/inventories/**").hasAnyRole(admin,warehouseManager)
                                .requestMatchers("/api/reservations/**").hasAnyRole(admin,warehouseManager)
                                .requestMatchers("/api/movements/**").hasAnyRole(admin,warehouseManager)
                                .requestMatchers("/api/carriers/**").hasAnyRole(admin,warehouseManager)
                                .requestMatchers("/api/shipments/**").hasAnyRole(admin,warehouseManager)
                                .requestMatchers("/api/sales-orders/**").hasAnyRole(admin,warehouseManager)
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/api/auth/register").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
