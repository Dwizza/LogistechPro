package com.logistechpro.Service;

import com.logistechpro.Mapper.POMapper;
import com.logistechpro.Repository.*;
import com.sun.source.tree.ModuleTree;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class POServiceTest {

    @Mock
    SupplierRepository supplierRepo;

    @Mock
    WarehouseRepository warehouseRepo;

    @Mock
    ProductRepository productRepo;

    @Mock
    PORepository poRepo;

    @Mock
    POMapper poMapper;

    @Mock
    InventoryRepository inventoryRepo;

    @Mock
    InventoryMovmentRepository movmentRepo;

    @InjectMocks
    POService poService;



}