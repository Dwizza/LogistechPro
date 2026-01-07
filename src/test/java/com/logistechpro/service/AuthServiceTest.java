package com.logistechpro.service;

import com.logistechpro.dto.request.ClientRegisterRequest;
import com.logistechpro.dto.response.ClientResponse;
import com.logistechpro.mapper.ClientMapper;
import com.logistechpro.models.Client;
import com.logistechpro.models.Enums.Role;
import com.logistechpro.models.User;
import com.logistechpro.repository.ClientRepository;
import com.logistechpro.repository.UserRepository;
import com.logistechpro.service.Implement.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock ClientRepository clientRepository;
    @Mock ClientMapper clientMapper;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks
    AuthServiceImpl service;

    ClientRegisterRequest reg() {
        ClientRegisterRequest r = new ClientRegisterRequest();
        r.setName("Client"); r.setEmail("c@test.com"); r.setPassword("pass123"); r.setTelephone("0600"); r.setAddress("addr");
        return r;
    }

    @Test
    void register_success() {
        ClientRegisterRequest r = reg();
        when(userRepository.findByEmail("c@test.com")).thenReturn(Optional.empty());
        Client clientEntity = Client.builder().id(7L).telephone("0600").address("addr").user(User.builder().id(10L).name("Client").email("c@test.com").passwordHash("enc").role(Role.CLIENT).active(true).build()).build();
        when(clientMapper.toEntity(r)).thenReturn(clientEntity);
        when(passwordEncoder.encode("pass123")).thenReturn("enc");
        when(userRepository.save(clientEntity.getUser())).thenReturn(clientEntity.getUser());
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);
        ClientResponse resp = ClientResponse.builder().id(7L).name("Client").email("c@test.com").role("CLIENT").build();
        when(clientMapper.toResponse(clientEntity)).thenReturn(resp);
        ClientResponse out = service.register(r);
        assertEquals(7L, out.getId());
        verify(userRepository).findByEmail("c@test.com");
        verify(passwordEncoder).encode("pass123");
    }

    @Test
    void register_emailExists() {
        when(userRepository.findByEmail("c@test.com")).thenReturn(Optional.of(new User()));
        assertThrows(RuntimeException.class, () -> service.register(reg()));
        verify(userRepository).findByEmail("c@test.com");
    }
}

