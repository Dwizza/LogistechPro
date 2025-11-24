package com.logistechpro.service;

import com.logistechpro.dto.Request.ClientLoginRequest;
import com.logistechpro.dto.Request.ClientRegisterRequest;
import com.logistechpro.dto.Response.ClientResponse;
import com.logistechpro.mapper.ClientMapper;
import com.logistechpro.models.Client;
import com.logistechpro.models.Enums.Role;
import com.logistechpro.models.User;
import com.logistechpro.repository.ClientRepository;
import com.logistechpro.repository.UserRepository;
import com.logistechpro.service.Implement.ClientAuthServiceImpl;
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
class ClientAuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock ClientRepository clientRepository;
    @Mock ClientMapper clientMapper;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks ClientAuthServiceImpl service;

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

    @Test
    void login_success() {
        ClientLoginRequest l = new ClientLoginRequest(); l.setEmail("c@test.com"); l.setPassword("pass123");
        User user = User.builder().id(10L).email("c@test.com").passwordHash("enc").role(Role.CLIENT).name("Client").active(true).build();
        Client client = Client.builder().id(7L).telephone("0600").address("addr").user(user).build();
        when(userRepository.findByEmail("c@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123","enc")).thenReturn(true);
        when(clientRepository.findByUser(user)).thenReturn(Optional.of(client));
        ClientResponse resp = ClientResponse.builder().id(7L).email("c@test.com").name("Client").role("CLIENT").build();
        when(clientMapper.toResponse(client)).thenReturn(resp);
        ClientResponse out = service.login(l);
        assertEquals(7L,out.getId());
        verify(userRepository).findByEmail("c@test.com");
        verify(clientRepository).findByUser(user);
    }

    @Test
    void login_emailNotFound() {
        ClientLoginRequest l = new ClientLoginRequest(); l.setEmail("x@test.com"); l.setPassword("p");
        when(userRepository.findByEmail("x@test.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.login(l));
    }

    @Test
    void login_invalidPassword() {
        ClientLoginRequest l = new ClientLoginRequest(); l.setEmail("c@test.com"); l.setPassword("bad");
        User user = User.builder().id(10L).email("c@test.com").passwordHash("enc").role(Role.CLIENT).name("Client").active(true).build();
        when(userRepository.findByEmail("c@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad","enc")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.login(l));
    }

    @Test
    void login_clientProfileMissing() {
        ClientLoginRequest l = new ClientLoginRequest(); l.setEmail("c@test.com"); l.setPassword("pass123");
        User user = User.builder().id(10L).email("c@test.com").passwordHash("enc").role(Role.CLIENT).name("Client").active(true).build();
        when(userRepository.findByEmail("c@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123","enc")).thenReturn(true);
        when(clientRepository.findByUser(user)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.login(l));
    }
}

