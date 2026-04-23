package com.example.service;

import com.example.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id) throws ResourceNotFoundException;
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category) throws ResourceNotFoundException;
    void deleteCategory(Long id) throws ResourceNotFoundException;
}
