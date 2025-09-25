package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        if (product.getIsActive() == false) {
            throw new RuntimeException("san pham chua Active");
        }

        return product;
    }

    public List<Product> getProductByCategory(Long categoryId) {

        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Category ID không hợp lệ");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchActiveProductsNoPage(keyword);
    }

    public Product createProduct(Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not founf with id " + product.getCategory().getId()));
        if (category.getIsActive() == false) {
            throw new RuntimeException("category chua Active");
        }
        // name khong duoc de trong
        if (product.getName() == null || product.getName().isBlank()) {
            throw new RuntimeException("Tên sản phẩm không được để trống");
        }

        // gia > 0
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new RuntimeException("Giá sản phẩm phải > 0");
        }

        // so luong phai > 0
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new RuntimeException("Số lượng sản phẩm phải >= 0");
        }

        product.setCategory(category);

        return productRepository.save(product);
    }

    public Product updatedProduct(Long id, Product product) {

        Product currenProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductId not found with id " + id));

        if (product.getIsActive() == null) {
            throw new RuntimeException("san pham chua Active");
        }

        // get category
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found with id" + product.getCategory().getId()));
        // update
        if (product.getName() != null && !product.getName().isBlank()) {
            currenProduct.setName(product.getName());
        }

        if (product.getDescription() != null) {
            currenProduct.setDescription(product.getDescription());
        }

        if (product.getPrice() != null && product.getPrice() > 0) {
            currenProduct.setPrice(product.getPrice());
        }

        if (product.getQuantity() != null && product.getQuantity() >= 0) {
            currenProduct.setQuantity(product.getQuantity());
        }

        if (product.getImageUrl() != null) {
            currenProduct.setImageUrl(product.getImageUrl());
        }

        if (category != null) {
            currenProduct.setCategory(category);
        }

        if (product.getLowStockThreshold() != null && product.getLowStockThreshold() >= 0) {
            currenProduct.setLowStockThreshold(product.getLowStockThreshold());
        }

        return productRepository.save(currenProduct);
    }

    public Product deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id" + id));
        product.setIsActive(false);
        return productRepository.save(product);
    }
}
