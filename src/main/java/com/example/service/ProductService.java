package com.example.service;

import com.example.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id) throws ResourceNotFoundException;
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product) throws ResourceNotFoundException;
    void deleteProduct(Long id) throws ResourceNotFoundException;
}
