package com.example.demo.service;

import com.example.demo.entity.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    // tạo categori
    public Category registerCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    // get category by id
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category khong ton tai voi id: " + id));
    }

    // update category by id
    public Category updateCategoryById(Long id, Category newcategory) {
        Category category = getCategoryById(id);
        if (newcategory.getDescription() != null) {
            category.setDescription(newcategory.getDescription());
        }
        if (newcategory.getImageUrl() != null) {
            category.setImageUrl(newcategory.getImageUrl());
        }
        if (newcategory.getName() != null) {
            category.setName(newcategory.getName());
        }
        return categoryRepository.save(category);
    }

    // delete category by id
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("khong tim thay Category co Id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
