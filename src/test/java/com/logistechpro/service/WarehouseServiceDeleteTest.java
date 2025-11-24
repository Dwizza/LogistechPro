package com.logistechpro.service;

import com.logistechpro.repository.WarehouseRepository;
import com.logistechpro.service.Implement.WarehouseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceDeleteTest {
    @Mock WarehouseRepository repository;
    @InjectMocks WarehouseServiceImpl service;

    @Test
    void deleteId() {
        service.delete(100L);
        verify(repository).deleteById(100L);
    }
}

