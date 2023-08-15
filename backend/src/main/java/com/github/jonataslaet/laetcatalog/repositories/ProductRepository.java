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
            SELECT DISTINCT prod.id, prod.name
            FROM tb_product prod
            INNER JOIN tb_product_category prodcat ON prodcat.product_id = prod.id
            WHERE (:categoryIds IS NULL OR prodcat.category_id IN :categoryIds)
            AND (LOWER(prod.name) LIKE LOWER(CONCAT('%',:name,'%')))
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
            SELECT DISTINCT prod.id, prod.name
            FROM tb_product prod
            INNER JOIN tb_product_category prodcat ON prodcat.product_id = prod.id
            WHERE (:categoryIds IS NULL OR prodcat.category_id IN :categoryIds)
            AND (LOWER(prod.name) LIKE LOWER(CONCAT('%',:name,'%')))
            ) AS tb_result
            """)
    Page<ProductProjection> findAllWithTheirCategories(String name, List<Long> categoryIds, Pageable pageable);
}
