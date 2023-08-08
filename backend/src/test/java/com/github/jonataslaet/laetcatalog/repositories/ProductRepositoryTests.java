package com.github.jonataslaet.laetcatalog.repositories;

import com.github.jonataslaet.laetcatalog.entities.Product;
import com.github.jonataslaet.laetcatalog.factories.Factory;
import com.github.jonataslaet.laetcatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
    }

    @Test
    public void deleteShouldDeleteProductWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> optionalProduct = productRepository.findById(existingId);
        Assertions.assertTrue(optionalProduct.isEmpty());
    }

    @Test
    public void findByIdShouldFindWhenIdExists() {
        Long currentCount = productRepository.count();
        productRepository.deleteById(existingId);
        Long newCount = productRepository.count();
        Assertions.assertFalse(productRepository.existsById(existingId));
        Assertions.assertEquals(currentCount - 1, newCount);
    }

    @Test
    public void saveShouldSaveWhenIdIsNull() {
        Product product = Factory.createProduct();
        Long currentCount = productRepository.count();
        Assertions.assertNull(product.getId());
        productRepository.save(product);
        Long newCount = productRepository.count();
        Assertions.assertNotNull(product.getId());
        Assertions.assertTrue(productRepository.existsById(existingId));
        Assertions.assertEquals(currentCount + 1, newCount);
    }

    @Test
    public void saveShouldUpdateWhenIdIsNotNull() {
        Product product = Factory.findProduct();
        Long currentCount = productRepository.count();
        Assertions.assertNotNull(product.getId());
        productRepository.save(product);
        Long newCount = productRepository.count();
        Assertions.assertNotNull(product.getId());
        Assertions.assertTrue(productRepository.existsById(existingId));
        Assertions.assertEquals(currentCount, newCount);
    }
}
