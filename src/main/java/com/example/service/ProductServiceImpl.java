package com.example.service;

import com.example.domain.Product;
import com.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        // Convert Iterable to List
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Product getProductById(Long id) throws ResourceNotFoundException {
        Optional<Product> optional = productRepository.findById(id);
        return optional.orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        // In a real scenario, additional validation or business rules would be applied here
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) throws ResourceNotFoundException {
        Product existing = getProductById(id);
        // Update fields – assuming all fields are allowed to be updated
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) throws ResourceNotFoundException {
        Product existing = getProductById(id);
        productRepository.delete(existing);
    }
}
