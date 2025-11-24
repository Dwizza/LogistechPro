package com.logistechpro.Service;

import com.logistechpro.DTO.Request.WarehouseRequest;
import com.logistechpro.DTO.Response.WarehouseResponse;
import com.logistechpro.Mapper.WarehouseMapper;
import com.logistechpro.Models.Warehouse;
import com.logistechpro.Repository.WarehouseRepository;
import com.logistechpro.Service.Implement.WarehouseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {
    @Mock WarehouseRepository repository;
    @Mock WarehouseMapper mapper;
    @InjectMocks WarehouseServiceImpl service;

    WarehouseRequest req() { WarehouseRequest r = new WarehouseRequest(); r.setCode("W1"); r.setName("Main"); r.setActive(true); return r; }

    @Test
    void getAll() {
        Warehouse w = Warehouse.builder().id(1L).code("W1").name("Main").active(true).build();
        when(repository.findAll()).thenReturn(List.of(w));
        WarehouseResponse r = new WarehouseResponse(); r.setId(1L); r.setCode("W1");
        when(mapper.toResponse(w)).thenReturn(r);
        List<WarehouseResponse> out = service.getAll();
        assertEquals(1,out.size());
        verify(repository).findAll();
    }

    @Test
    void getById_found() {
        Warehouse w = Warehouse.builder().id(2L).code("W2").name("Sec").active(true).build();
        when(repository.findById(2L)).thenReturn(Optional.of(w));
        WarehouseResponse r = new WarehouseResponse(); r.setId(2L); r.setCode("W2");
        when(mapper.toResponse(w)).thenReturn(r);
        WarehouseResponse out = service.getById(2L);
        assertEquals(2L,out.getId());
    }

    @Test
    void getById_notFound() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getById(9L));
    }

    @Test
    void create_success() {
        WarehouseRequest r = req();
        when(repository.findByCode("W1")).thenReturn(Optional.empty());
        Warehouse entity = Warehouse.builder().id(4L).code("W1").name("Main").active(true).build();
        when(mapper.toEntity(r)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        WarehouseResponse resp = new WarehouseResponse(); resp.setId(4L); resp.setCode("W1");
        when(mapper.toResponse(entity)).thenReturn(resp);
        WarehouseResponse out = service.create(r);
        assertEquals(4L,out.getId());
        verify(repository).findByCode("W1");
    }

    @Test
    void create_codeExists() {
        when(repository.findByCode("W1")).thenReturn(Optional.of(new Warehouse()));
        assertThrows(RuntimeException.class, () -> service.create(req()));
    }

    @Test
    void update_success() {
        Warehouse existing = Warehouse.builder().id(5L).code("OLD").name("Old").active(false).build();
        WarehouseRequest r = req();
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        WarehouseResponse resp = new WarehouseResponse(); resp.setId(5L); resp.setCode("W1");
        when(mapper.toResponse(existing)).thenReturn(resp);
        WarehouseResponse out = service.update(5L,r);
        assertEquals("W1", out.getCode());
        assertTrue(existing.isActive());
    }

    @Test
    void update_notFound() {
        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.update(5L, req()));
    }

    @Test
    void delete_callsRepo() {
        service.delete(6L);
        verify(repository).deleteById(6L);
    }
}

