package com.github.jonataslaet.laetcatalog.controllers;

import com.github.jonataslaet.laetcatalog.controllers.dtos.ProductDTO;
import com.github.jonataslaet.laetcatalog.entities.ProductProjection;
import com.github.jonataslaet.laetcatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAllPaged(Pageable pageable) {
        Page<ProductDTO> categories = productService.findAllPaged(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/withtheircategories")
    public ResponseEntity<Page<ProductProjection>> findAllPagedWithTheirCategories(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "categoryIds", defaultValue = "") String categoryIds,
            Pageable pageable) {
        Page<ProductProjection> products = productService.findAllPagedWithTheirCategories(
                name, categoryIds, pageable
        );
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable("id") Long id) {
        ProductDTO productDTO = productService.findById(id);
        return ResponseEntity.ok(productDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDTO = productService.insert(productDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedProductDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(savedProductDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable("id") Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedCategory = productService.update(id, productDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
