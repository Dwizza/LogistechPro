package com.logistechpro.config;

import com.logistechpro.repository.UserRepository;
import com.logistechpro.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.core.userdetails.User.withUsername;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String WAREHOUSE_MANAGER = "WAREHOUSE_MANAGER";

    private final UserRepository userRepository;


    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository repo) {
        return email -> repo.findByEmail(email)
                .map(u -> User.withUsername(u.getEmail())
                        .password(u.getPasswordHash())
                        .roles(u.getRole().name())
                        .build())
                .orElseThrow();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
                .cors().and()
                .csrf().disable()
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers("/api/admin/**").hasRole(ADMIN)
                        .requestMatchers("/api/products/**").hasRole(ADMIN)
                        .requestMatchers("/api/suppliers/**").hasRole(ADMIN)
                        .requestMatchers("/api/warehouses/**").hasRole(ADMIN)

                        .requestMatchers("/api/purchase-orders/**").hasAnyRole(ADMIN, WAREHOUSE_MANAGER)
                        .requestMatchers("/api/inventories/**").hasAnyRole(ADMIN, WAREHOUSE_MANAGER)
                        .requestMatchers("/api/reservations/**").hasAnyRole(ADMIN, WAREHOUSE_MANAGER)
                        .requestMatchers("/api/movements/**").hasAnyRole(ADMIN, WAREHOUSE_MANAGER)
                        .requestMatchers("/api/carriers/**").hasAnyRole(ADMIN, WAREHOUSE_MANAGER)
                        .requestMatchers("/api/shipments/**").hasAnyRole(ADMIN, WAREHOUSE_MANAGER)
                        .requestMatchers("/api/sales-orders/**").hasAnyRole(ADMIN, WAREHOUSE_MANAGER)

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
