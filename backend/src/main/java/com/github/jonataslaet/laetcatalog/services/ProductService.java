package com.github.jonataslaet.laetcatalog.services;

import com.github.jonataslaet.laetcatalog.controllers.dtos.CategoryDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.ProductDTO;
import com.github.jonataslaet.laetcatalog.entities.Category;
import com.github.jonataslaet.laetcatalog.entities.Product;
import com.github.jonataslaet.laetcatalog.entities.ProductProjection;
import com.github.jonataslaet.laetcatalog.repositories.CategoryRepository;
import com.github.jonataslaet.laetcatalog.repositories.ProductRepository;
import com.github.jonataslaet.laetcatalog.services.exceptions.DatabaseException;
import com.github.jonataslaet.laetcatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ProductProjection> findAllPagedWithTheirCategories(String name, String categoryIds, Pageable pageable) {
        List<Long> categoryLongIds = getCategoryLongIds(categoryIds);
        Page<ProductProjection> products =
                productRepository.findAllWithTheirCategories(name, categoryLongIds, pageable);
        return products;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        Product product = optionalProduct.orElseThrow(() -> new ResourceNotFoundException("Product not found for id = " + id));
        return new ProductDTO(product, product.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {
        Product product = new Product();
        setProductFromDTO(product, productDTO);
        productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        try {
            Product product = productRepository.getReferenceById(id);
            setProductFromDTO(product, productDTO);
            product = productRepository.save(product);
            return new ProductDTO(product);
        } catch (EntityNotFoundException entityNotFoundException){
            throw new ResourceNotFoundException("Product not found for id = " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(("Resource not found"));
        }
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new DatabaseException("Integrity Violation");
        }
    }

    private void setProductFromDTO(Product product, ProductDTO productDTO) {
        product.setName(productDTO.getName());
        product.setDate(productDTO.getDate());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImgUrl(productDTO.getImgUrl());
        product.getCategories().clear();
        for (CategoryDTO categoryDTO: productDTO.getCategories()) {
            Category category = categoryRepository.getReferenceById(categoryDTO.getId());
            product.getCategories().add(category);
        }
    }

    private List<Long> getCategoryLongIds(String categoryIds) {
        if (categoryIds.isEmpty()) return new ArrayList<>();
        return Arrays.stream(categoryIds.split(",")).map(Long::parseLong).toList();
    }
}
