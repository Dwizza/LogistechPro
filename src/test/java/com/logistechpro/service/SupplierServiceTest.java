package com.logistechpro.service;

import com.logistechpro.dto.request.SupplierRequest;
import com.logistechpro.dto.response.SupplierResponse;
import com.logistechpro.mapper.SupplierMapper;
import com.logistechpro.models.Supplier;
import com.logistechpro.repository.SupplierRepository;
import com.logistechpro.service.Implement.SupplierServiceImpl;
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
class SupplierServiceTest {
    @Mock SupplierRepository supplierRepository;
    @Mock SupplierMapper mapper;
    @InjectMocks SupplierServiceImpl service;

    SupplierRequest req() { SupplierRequest r = new SupplierRequest(); r.setName("S1"); r.setContactInfo("s1@mail.com"); return r; }

    @Test
    void getAll() {
        Supplier s = Supplier.builder().id(1L).name("S1").contactInfo("c").build();
        when(supplierRepository.findAll()).thenReturn(List.of(s));
        SupplierResponse resp = new SupplierResponse(); resp.setId(1L); resp.setName("S1");
        when(mapper.toResponse(s)).thenReturn(resp);
        List<SupplierResponse> out = service.getAll();
        assertEquals(1,out.size());
        verify(supplierRepository).findAll();
    }

    @Test
    void getById_found() {
        Supplier s = Supplier.builder().id(2L).name("S2").contactInfo("c").build();
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(s));
        SupplierResponse resp = new SupplierResponse(); resp.setId(2L); resp.setName("S2");
        when(mapper.toResponse(s)).thenReturn(resp);
        SupplierResponse out = service.getById(2L);
        assertEquals(2L,out.getId());
    }

    @Test
    void getById_notFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getById(99L));
    }

    @Test
    void create_success() {
        SupplierRequest r = req();
        Supplier entity = Supplier.builder().id(5L).name("S1").contactInfo("s1@mail.com").build();
        when(mapper.toEntity(r)).thenReturn(entity);
        when(supplierRepository.save(entity)).thenReturn(entity);
        SupplierResponse resp = new SupplierResponse(); resp.setId(5L); resp.setName("S1");
        when(mapper.toResponse(entity)).thenReturn(resp);
        SupplierResponse out = service.create(r);
        assertEquals(5L,out.getId());
        verify(supplierRepository).save(entity);
    }

    @Test
    void update_success() {
        Supplier existing = Supplier.builder().id(6L).name("Old").contactInfo("old@mail.com").build();
        SupplierRequest r = req();
        when(supplierRepository.findById(6L)).thenReturn(Optional.of(existing));
        when(supplierRepository.save(existing)).thenReturn(existing);
        doAnswer(a -> { existing.setName(r.getName()); return null; }).when(mapper).updateEntityFromDto(r, existing);
        SupplierResponse resp = new SupplierResponse(); resp.setId(6L); resp.setName("S1");
        when(mapper.toResponse(existing)).thenReturn(resp);
        SupplierResponse out = service.update(6L,r);
        assertEquals("S1", out.getName());
        verify(mapper).updateEntityFromDto(r, existing);
    }

    @Test
    void update_notFound() {
        when(supplierRepository.findById(6L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.update(6L, req()));
    }

    @Test
    void delete_success() {
        when(supplierRepository.existsById(7L)).thenReturn(true);
        service.delete(7L);
        verify(supplierRepository).deleteById(7L);
    }

    @Test
    void delete_notFound() {
        when(supplierRepository.existsById(8L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.delete(8L));
    }
}

