package com.logistechpro.service.Implement;

import com.logistechpro.dto.Request.LoginRequest;
import com.logistechpro.dto.Request.ClientRegisterRequest;
import com.logistechpro.dto.Response.AuthResponse;
import com.logistechpro.dto.Response.ClientResponse;
import com.logistechpro.mapper.ClientMapper;
import com.logistechpro.models.Client;
import com.logistechpro.models.User;
import com.logistechpro.repository.ClientRepository;
import com.logistechpro.repository.UserRepository;
import com.logistechpro.security.JwtService;
import com.logistechpro.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public ClientResponse register(ClientRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        Client client = clientMapper.toEntity(request);
        client.getUser().setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(client.getUser());
        client.setUser(savedUser);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponse(savedClient);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }
}
