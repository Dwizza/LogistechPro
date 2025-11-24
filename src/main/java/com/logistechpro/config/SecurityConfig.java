package com.logistechpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
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

    private static final String WAREHOUSE_MANAGER = "WAREHOUSE_MANAGER";
    private static final String ADMIN = "ADMIN";

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
                throw new BadCredentialsException("Invalid credentials");
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
                                .requestMatchers("/api/admin/**").hasRole(ADMIN)
                                .requestMatchers("/api/products/**").hasRole(ADMIN)
                                .requestMatchers("/api/suppliers/**").hasRole(ADMIN)
                                .requestMatchers("/api/warehouses/**").hasRole(ADMIN)
                                .requestMatchers("/api/purchase-orders/**").hasAnyRole(ADMIN,WAREHOUSE_MANAGER)
                                .requestMatchers("/api/inventories/**").hasAnyRole(ADMIN,WAREHOUSE_MANAGER)
                                .requestMatchers("/api/reservations/**").hasAnyRole(ADMIN,WAREHOUSE_MANAGER)
                                .requestMatchers("/api/movements/**").hasAnyRole(ADMIN,WAREHOUSE_MANAGER)
                                .requestMatchers("/api/carriers/**").hasAnyRole(ADMIN,WAREHOUSE_MANAGER)
                                .requestMatchers("/api/shipments/**").hasAnyRole(ADMIN,WAREHOUSE_MANAGER)
                                .requestMatchers("/api/sales-orders/**").hasAnyRole(ADMIN,WAREHOUSE_MANAGER)
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/api/auth/register").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
