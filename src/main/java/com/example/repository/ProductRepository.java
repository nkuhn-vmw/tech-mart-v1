package com.example.repository;

import com.example.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find all products belonging to a specific category by its ID
    List<Product> findByCategoryId(Long categoryId);
}