package com.logistechpro.service.Implement;

import com.logistechpro.dto.Request.ClientLoginRequest;
import com.logistechpro.dto.Request.ClientRegisterRequest;
import com.logistechpro.dto.Response.ClientResponse;
import com.logistechpro.mapper.ClientMapper;
import com.logistechpro.models.Client;
import com.logistechpro.models.User;
import com.logistechpro.repository.ClientRepository;
import com.logistechpro.repository.UserRepository;
import com.logistechpro.service.ClientAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAuthServiceImpl implements ClientAuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ClientResponse register(ClientRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        Client client = clientMapper.toEntity(request);
        // Encoder le mot de passe avant sauvegarde
        client.getUser().setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(client.getUser());
        client.setUser(savedUser);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponse(savedClient);
    }

    @Override
    public ClientResponse login(ClientLoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        return clientMapper.toResponse(client);
    }

}
