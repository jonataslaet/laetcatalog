package com.github.jonataslaet.laetcatalog.repositories;

import com.github.jonataslaet.laetcatalog.entities.Product;
import com.github.jonataslaet.laetcatalog.entities.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true, value = """
            SELECT DISTINCT tb_product.id, tb_product.name
            FROM tb_product
            INNER JOIN tb_product_category ON tb_product_category.product_id = tb_product.id
            WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND (LOWER(tb_product.name) LIKE LOWER(CONCAT('%',:name,'%')))
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
            SELECT DISTINCT tb_product.id, tb_product.name
            FROM tb_product
            INNER JOIN tb_product_category ON tb_product_category.product_id = tb_product.id
            WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND (LOWER(tb_product.name) LIKE LOWER(CONCAT('%',:name,'%')))
            ) AS tb_result
            """)
    Page<ProductProjection> findAllWithTheirCategories(String name, List<Long> categoryIds, Pageable pageable);
}
