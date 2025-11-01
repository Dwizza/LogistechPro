package com.logistechpro.Service.Implement;

import com.logistechpro.DTO.Request.ClientLoginRequest;
import com.logistechpro.DTO.Request.ClientRegisterRequest;
import com.logistechpro.DTO.Response.ClientResponse;
import com.logistechpro.Mapper.ClientMapper;
import com.logistechpro.Models.Client;
import com.logistechpro.Models.User;
import com.logistechpro.Repository.ClientRepository;
import com.logistechpro.Repository.UserRepository;
import com.logistechpro.Service.ClientAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientAuthServiceImpl implements ClientAuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
//    private final PasswordEncoder passwordEncoder;

    @Override
    public ClientResponse register(ClientRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        Client client = clientMapper.toEntity(request);
//        client.getUser().setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(client.getUser());
        client.setUser(savedUser);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponse(savedClient);
    }

    @Override
    public ClientResponse login(ClientLoginRequest request) {


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        return clientMapper.toResponse(client);
    }

}

