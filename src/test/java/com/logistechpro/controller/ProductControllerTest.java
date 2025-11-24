//package com.logistechpro.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.logistechpro.DTO.Request.ProductRequest;
//import com.logistechpro.DTO.Response.ProductResponse;
//import com.logistechpro.Service.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductControllerTest {
//
//    @Mock
//    private ProductService productService;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setup() {
//        ProductController controller = new ProductController(productService);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void getAll_shouldReturnOkWithList() throws Exception {
//        ProductResponse r1 = new ProductResponse(); r1.setId(1L); r1.setSku("SKU1"); r1.setName("A"); r1.setCategory("C1"); r1.setAvgPrice(new BigDecimal("10.50")); r1.setActive(true);
//        ProductResponse r2 = new ProductResponse(); r2.setId(2L); r2.setSku("SKU2"); r2.setName("B"); r2.setCategory("C2"); r2.setAvgPrice(new BigDecimal("20.00")); r2.setActive(false);
//        given(productService.getAll()).willReturn(List.of(r1, r2));
//
//        mockMvc.perform(get("/api/products"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].sku").value("SKU1"))
//                .andExpect(jsonPath("$[1].sku").value("SKU2"));
//    }
//
//    @Test
//    void getById_shouldReturnOkWithItem() throws Exception {
//        ProductResponse r = new ProductResponse(); r.setId(7L); r.setSku("SKU7"); r.setName("P7"); r.setCategory("C7"); r.setAvgPrice(new BigDecimal("7.77")); r.setActive(true);
//        given(productService.getById(7L)).willReturn(r);
//
//        mockMvc.perform(get("/api/products/{id}", 7L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(7))
//                .andExpect(jsonPath("$.sku").value("SKU7"));
//    }
//
//    @Test
//    void getBySku_shouldReturnOkWithItem() throws Exception {
//        ProductResponse r = new ProductResponse(); r.setId(5L); r.setSku("X-5"); r.setName("PX"); r.setCategory("CX"); r.setAvgPrice(new BigDecimal("5.50")); r.setActive(true);
//        given(productService.getBySku("X-5")).willReturn(r);
//
//        mockMvc.perform(get("/api/products/sku/{sku}", "X-5"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.sku").value("X-5"));
//    }
//
//    @Test
//    void create_shouldReturnOkWithCreatedItem() throws Exception {
//        ProductRequest req = new ProductRequest();
//        req.setSku("NEW"); req.setName("N"); req.setCategory("C"); req.setAvgPrice(new BigDecimal("9.99")); req.setWarehouseId(1L); req.setActive(true);
//
//        ProductResponse out = new ProductResponse(); out.setId(100L); out.setSku("NEW"); out.setName("N"); out.setCategory("C"); out.setAvgPrice(new BigDecimal("9.99")); out.setActive(true);
//        given(productService.create(any(ProductRequest.class))).willReturn(out);
//
//        mockMvc.perform(post("/api/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(100))
//                .andExpect(jsonPath("$.sku").value("NEW"));
//    }
//
//    @Test
//    void update_shouldReturnOkWithUpdatedItem() throws Exception {
//        ProductRequest req = new ProductRequest();
//        req.setSku("UP"); req.setName("U"); req.setCategory("UC"); req.setAvgPrice(new BigDecimal("12.34")); req.setWarehouseId(2L); req.setActive(false);
//
//        ProductResponse out = new ProductResponse(); out.setId(11L); out.setSku("UP"); out.setName("U"); out.setCategory("UC"); out.setAvgPrice(new BigDecimal("12.34")); out.setActive(false);
//        given(productService.update(eq(11L), any(ProductRequest.class))).willReturn(out);
//
//        mockMvc.perform(put("/api/products/{id}", 11L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(11))
//                .andExpect(jsonPath("$.name").value("U"));
//    }
//
//    @Test
//    void delete_shouldReturnNoContent() throws Exception {
//        mockMvc.perform(delete("/api/products/{id}", 9L))
//                .andExpect(status().isNoContent());
//    }
//}
