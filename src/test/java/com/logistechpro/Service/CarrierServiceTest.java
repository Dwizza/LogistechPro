package com.logistechpro.Service;

import com.logistechpro.DTO.Request.CarrierRequest;
import com.logistechpro.DTO.Response.CarrierResponse;
import com.logistechpro.Mapper.CarrierMapper;
import com.logistechpro.Models.Carrier;
import com.logistechpro.Models.Enums.CarrierStatus;
import com.logistechpro.Repository.CarrierRepository;
import com.logistechpro.Service.Implement.CarrierServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierServiceTest {
    @Mock CarrierRepository carrierRepo;
    @Mock CarrierMapper carrierMapper;
    @InjectMocks CarrierServiceImpl service;

    CarrierRequest req() {
        CarrierRequest r = new CarrierRequest();
        r.setName("C1");
        r.setContactEmail("c1@mail.com");
        r.setCapacityWeight("100");
        r.setCapacityVolume("200");
        r.setStatus("ACTIVE");
        return r;
    }

    @Test
    void createCarrier_success() {
        CarrierRequest r = req();
        when(carrierRepo.findByContactEmail("c1@mail.com")).thenReturn(Optional.empty());
        Carrier entity = Carrier.builder().id(1L).name("C1").contactEmail("c1@mail.com").capacityWeight(BigDecimal.TEN).capacityVolume(BigDecimal.ONE).active(true).status(CarrierStatus.ACTIVE).build();
        when(carrierMapper.toEntity(r)).thenReturn(entity);
        when(carrierRepo.save(entity)).thenReturn(entity);
        CarrierResponse resp = new CarrierResponse();
        resp.setId(1L); resp.setName("C1");
        when(carrierMapper.toResponse(entity)).thenReturn(resp);
        CarrierResponse out = service.createCarrier(r);
        assertEquals(1L, out.getId());
        assertEquals("C1", out.getName());
        verify(carrierRepo).findByContactEmail("c1@mail.com");
        verify(carrierMapper).toEntity(r);
        verify(carrierRepo).save(entity);
        verify(carrierMapper).toResponse(entity);
    }

    @Test
    void createCarrier_emailExists() {
        when(carrierRepo.findByContactEmail("c1@mail.com")).thenReturn(Optional.of(new Carrier()));
        assertThrows(RuntimeException.class, () -> service.createCarrier(req()));
        verify(carrierRepo).findByContactEmail("c1@mail.com");
        verify(carrierRepo, never()).save(any());
        verify(carrierMapper, never()).toResponse(any());
    }

    @Test
    void getAllCarriers_list() {
        Carrier c1 = Carrier.builder().id(1L).name("A").contactEmail("a@x.com").build();
        Carrier c2 = Carrier.builder().id(2L).name("B").contactEmail("b@x.com").build();
        when(carrierRepo.findAll()).thenReturn(List.of(c1,c2));
        CarrierResponse r1 = new CarrierResponse(); r1.setId(1L); r1.setName("A");
        CarrierResponse r2 = new CarrierResponse(); r2.setId(2L); r2.setName("B");
        when(carrierMapper.toResponse(c1)).thenReturn(r1);
        when(carrierMapper.toResponse(c2)).thenReturn(r2);
        List<CarrierResponse> out = service.getAllCarriers();
        assertEquals(2, out.size());
        verify(carrierRepo).findAll();
        verify(carrierMapper).toResponse(c1);
        verify(carrierMapper).toResponse(c2);
    }

    @Test
    void getAllCarriers_empty() {
        when(carrierRepo.findAll()).thenReturn(List.of());
        List<CarrierResponse> out = service.getAllCarriers();
        assertTrue(out.isEmpty());
        verify(carrierRepo).findAll();
        verify(carrierMapper, never()).toResponse(any());
    }
}
