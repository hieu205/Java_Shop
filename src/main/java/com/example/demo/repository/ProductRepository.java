package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();

    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    @Query(value = "SELECT p.* FROM products p " +
            "JOIN categories c ON p.category_id = c.id " +
            "WHERE p.is_active = 1 " +
            "AND c.is_active = 1 " +
            "AND (LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(p.description) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(c.name) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(c.description) LIKE CONCAT('%', LOWER(:keyword), '%'))", nativeQuery = true)
    List<Product> searchActiveProductsNoPage(@Param("keyword") String keyword);
}
