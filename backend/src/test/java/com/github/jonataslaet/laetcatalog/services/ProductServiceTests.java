package com.github.jonataslaet.laetcatalog.services;

import com.github.jonataslaet.laetcatalog.controllers.dtos.ProductDTO;
import com.github.jonataslaet.laetcatalog.entities.Product;
import com.github.jonataslaet.laetcatalog.factories.Factory;
import com.github.jonataslaet.laetcatalog.repositories.CategoryRepository;
import com.github.jonataslaet.laetcatalog.repositories.ProductRepository;
import com.github.jonataslaet.laetcatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void deleteShouldThrowNotFoundExceptionWhenIdNotExists() {
        Long nonExistingId = 999L;
        Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldDeleteProductWhenIdExists() {
        Long existingId = 1L;
        Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
        Mockito.doNothing().when(productRepository).deleteById(existingId);
        productService.delete(existingId);
        Mockito.verify(productRepository).deleteById(existingId);
    }
}
