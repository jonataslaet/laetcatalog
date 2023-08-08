package com.github.jonataslaet.laetcatalog.factories;

import com.github.jonataslaet.laetcatalog.controllers.dtos.ProductDTO;
import com.github.jonataslaet.laetcatalog.entities.Category;
import com.github.jonataslaet.laetcatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product findProduct() {
        Product product = new Product(1L, "Smartphone", "Good", 800.0, "https://img.com/img.png", Instant.parse("2020-03-23T03:00:00Z"));
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDTO findProductDTO() {
        Product product = findProduct();
        return new ProductDTO(product, product.getCategories());
    }

    public static Product createProduct() {
        Product product = new Product(null, "Phone", "Good", 800.0, "https://img.com/img.png", Instant.parse("2020-03-23T03:00:00Z"));
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
