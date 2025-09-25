package com.example.demo.controller;

import com.example.demo.entity.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CategoryService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    // //
    // // create category
    // @PostMapping("/register")
    // public ResponseEntity<Category> registerCategory(@RequestBody Category
    // categori) {
    // return ResponseEntity.ok(categoryService.registerCategory(categori));
    // }

    // // Get category by id
    // @GetMapping("/{id}")
    // public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
    // return ResponseEntity.ok(categoryService.getCategoryById(id));
    // }

    // // get categoryIsActive
    // @GetMapping
    // public ResponseEntity<List<Category>> getCategoryIsActive() {
    // return ResponseEntity.ok(categoryService.getIsActiveCategory());
    // }

    // // Update Category by id
    // @PutMapping("/{id}")
    // public ResponseEntity<Category> updateCategoriById(@PathVariable Long id,
    // @RequestBody Category category) {
    // return ResponseEntity.ok(categoryService.updateCategoryById(id, category));
    // }

    // // Delete Category By Id
    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
    // categoryService.deleteCategoryById(id);
    // return ResponseEntity.ok("Xoa category thanh cong");
    // }

    // lay tat ca cac danh muc dang hoat dong
    @GetMapping()
    public ResponseEntity<List<Category>> getAllActiveCategories() {
        return ResponseEntity.ok(categoryService.getAllActiveCategories());
    }

    // lay chi tiet danh muc theo Id
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // lay danh muc con theo Parent Id
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Category>> getCategoriesByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubCategories(parentId));
    }

    // tao danh muc moi ( chi co Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    // cap nhat dang muc (chi co Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategoryById(@PathVariable Long id, @Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategoryById(id, category));
    }

    // xoa danh muc by Id
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}
