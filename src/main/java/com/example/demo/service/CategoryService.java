package com.example.demo.service;

import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;

// import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.repository.CategoryRepository;

import jakarta.websocket.server.PathParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : categories) {
            categoryResponses.add(CategoryResponse.fromEntity(category));
        }
        return categoryResponses;
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        if (category.getIsActive() == false) {
            throw new RuntimeException("Categoty not Active");
        }
        return CategoryResponse.fromEntity(category);
    }

    public List<CategoryResponse> getSubCategories(Long parentCategoryId) {
        List<Category> categories = categoryRepository.findActiveSubCategories(parentCategoryId);
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : categories) {
            categoryResponses.add(CategoryResponse.fromEntity(category));
        }
        return categoryResponses;
    }

    public CategoryResponse createCategory(Category category) {

        // Kiểm tra parent category (nếu có)
        Category parentCategory = null;
        if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
            parentCategory = categoryRepository.findById(category.getParentCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id "
                            + category.getParentCategory().getId()));

            if (!parentCategory.getIsActive()) {
                throw new RuntimeException("Parent category is not active");
            }
        }

        // Kiểm tra trùng tên
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category name already exists: " + category.getName());
        }

        // Tạo category mới
        Category newCategory = Category.builder()
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentCategory(parentCategory)
                .isActive(true)
                .build();

        // Lưu vào DB
        categoryRepository.save(newCategory);
        return CategoryResponse.fromEntity(newCategory);
    }

    public CategoryResponse updateCategoryById(Long id, Category updateCategory) {
        // Lấy category từ DB
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Kiểm tra parent category nếu thay đổi
        Long currentParentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
        Long requestParentId = updateCategory.getParentCategory() != null ? updateCategory.getParentCategory().getId()
                : null;

        if (!Objects.equals(currentParentId, requestParentId)) {
            if (requestParentId != null) {
                // Gán parent category mới
                Category parentCategory = categoryRepository.findById(requestParentId)
                        .orElseThrow(
                                () -> new RuntimeException("Parent category not found with id: " + requestParentId));

                if (!parentCategory.getIsActive()) {
                    throw new RuntimeException("Parent category is not active");
                }
                category.setParentCategory(parentCategory);
            } else {
                // Gán về null nếu muốn thành root category
                category.setParentCategory(null);
            }
        }

        // Kiểm tra tên mới đã tồn tại chưa (trừ chính nó)
        if (!category.getName().equals(updateCategory.getName())
                && categoryRepository.existsByName(updateCategory.getName())) {
            throw new RuntimeException("Category name already exists: " + updateCategory.getName());
        }

        // Cập nhật các trường khác
        category.setName(updateCategory.getName());
        category.setDescription(updateCategory.getDescription());
        category.setImageUrl(updateCategory.getImageUrl());
        category.setIsActive(true); // luôn active

        categoryRepository.save(category);

        return CategoryResponse.fromEntity(category);
    }

    public CategoryResponse deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if category has products
        if (!category.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot delete category that has products");
        }

        // Check if category has subcategories
        if (!category.getSubCategories().isEmpty()) {
            throw new RuntimeException("Cannot delete category that has subcategories");
        }

        // Soft delete
        category.setIsActive(false);
        categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

}
