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
import com.logistechpro.Repository.UserRepository;
import com.logistechpro.Models.User;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    private final String warehouse_manager = "WAREHOUSE_MANAGER";

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
                throw new UsernameNotFoundException("User inactive: " + username);
            }
            return org.springframework.security.core.userdetails.User.withUsername(domainUser.getEmail())
                    .password(domainUser.getPasswordHash())
                    .roles(domainUser.getRole().name())
                    .disabled(!domainUser.isActive())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/products/**").hasRole("ADMIN")
                                .requestMatchers("/api/suppliers/**").hasRole("ADMIN")
                                .requestMatchers("/api/warehouses/**").hasRole("ADMIN")
                                .requestMatchers("/api/purchase-orders/**").hasAnyRole("ADMIN",warehouse_manager)
                                .requestMatchers("/api/inventories/**").hasAnyRole("ADMIN",warehouse_manager)
                                .requestMatchers("/api/reservations/**").hasAnyRole("ADMIN",warehouse_manager)
                                .requestMatchers("/api/movements/**").hasAnyRole("ADMIN",warehouse_manager)
                                .requestMatchers("/api/carriers/**").hasAnyRole("ADMIN",warehouse_manager)
                                .requestMatchers("/api/shipments/**").hasAnyRole("ADMIN",warehouse_manager)
                                .requestMatchers("/api/sales-orders/**").hasAnyRole("ADMIN",warehouse_manager)
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/api/auth/register").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
