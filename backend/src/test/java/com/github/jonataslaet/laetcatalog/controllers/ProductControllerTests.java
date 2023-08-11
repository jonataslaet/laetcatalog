package com.github.jonataslaet.laetcatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jonataslaet.laetcatalog.controllers.dtos.ProductDTO;
import com.github.jonataslaet.laetcatalog.factories.Factory;
import com.github.jonataslaet.laetcatalog.utils.TokenUtil;
import com.github.jonataslaet.laetcatalog.services.ProductService;
import com.github.jonataslaet.laetcatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTests {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Long existingId;
    private Long nonExistingId;

    private ProductDTO createdProductDTO;
    private ProductDTO foundProductDTO;
    private PageImpl<ProductDTO> productsPage;
    private final ResourceNotFoundException resourceNotFoundException =
            new ResourceNotFoundException("Product not found for id = " + nonExistingId);

    @Autowired
    private ObjectMapper objectMapper;

    private String adminUsername;
    private String adminPassword;

    @BeforeEach
    public void setUp() {
        createdProductDTO = Factory.createProductDTO();
        foundProductDTO = Factory.findProductDTO();
        productsPage = new PageImpl<>(List.of(foundProductDTO));
        existingId = foundProductDTO.getId();
        nonExistingId = 999L;
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        when(productService.findAllPaged(any())).thenReturn(productsPage);
        ResultActions performRequest =
                mockMvc.perform(MockMvcRequestBuilders.get("/products"));
        performRequest.andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldFindWhenIdExists() throws Exception {
        when(productService.findById(existingId)).thenReturn(foundProductDTO);
        ResultActions performRequest = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId));
        performRequest.andExpect(status().isOk());
        performRequest.andExpect(jsonPath("$.id").exists());
        performRequest.andExpect(jsonPath("$.name").exists());
        performRequest.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundExceptionWhenIdNotExists() throws Exception {
        when(productService.findById(nonExistingId)).thenThrow(resourceNotFoundException);
        ResultActions performRequest = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId));
        performRequest.andExpect(status().isNotFound());
        performRequest.andExpect(jsonPath("$.timestamp").exists());
        performRequest.andExpect(jsonPath("$.status").exists());
        performRequest.andExpect(jsonPath("$.error").exists());
        performRequest.andExpect(jsonPath("$.message").exists());
        performRequest.andExpect(jsonPath("$.path").exists());
    }

    @Test
    public void updateShouldUpdateWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonFoundProduct = objectMapper.writeValueAsString(foundProductDTO);
        when(productService.update(eq(existingId), any())).thenReturn(foundProductDTO);
        ResultActions performRequest = mockMvc.perform(
                MockMvcRequestBuilders.put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonFoundProduct)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        performRequest.andExpect(status().isOk());
        performRequest.andExpect(jsonPath("$.id").exists());
        performRequest.andExpect(jsonPath("$.name").exists());
        performRequest.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundExceptionWhenIdNotExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        when(productService.update(eq(nonExistingId), any())).thenThrow(resourceNotFoundException);
        String jsonFoundProduct = objectMapper.writeValueAsString(foundProductDTO);
        ResultActions performRequest = mockMvc.perform(
                MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonFoundProduct)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        performRequest.andExpect(status().isNotFound());
        performRequest.andExpect(jsonPath("$.timestamp").exists());
        performRequest.andExpect(jsonPath("$.status").exists());
        performRequest.andExpect(jsonPath("$.error").exists());
        performRequest.andExpect(jsonPath("$.message").exists());
        performRequest.andExpect(jsonPath("$.path").exists());
    }

    @Test
    public void deleteShouldDeleteWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        doNothing().when(productService).delete(existingId);
        ResultActions performRequest = mockMvc.perform(
                MockMvcRequestBuilders.delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        performRequest.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundExceptionWhenIdNotExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        doThrow(resourceNotFoundException).when(productService).delete(nonExistingId);
        ResultActions performRequest = mockMvc.perform(
                MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        performRequest.andExpect(status().isNotFound());
    }

    @Test
    public void createShouldCreateWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonCreateProduct = objectMapper.writeValueAsString(createdProductDTO);
        createdProductDTO.setId(1L);
        when(productService.insert(any())).thenReturn(createdProductDTO);
        ResultActions performRequest = mockMvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonCreateProduct)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        performRequest.andExpect(status().isCreated());
        performRequest.andExpect(jsonPath("$.id").exists());
        performRequest.andExpect(jsonPath("$.name").exists());
        performRequest.andExpect(jsonPath("$.description").exists());
    }

}
