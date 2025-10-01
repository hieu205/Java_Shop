package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;
import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

import lombok.Builder;

import java.util.List;

@Builder
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(ProductResponse.fromEntity(product));
        }
        return productResponses;
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        if (product.getIsActive() == false) {
            throw new RuntimeException("san pham chua Active");
        }

        return ProductResponse.fromEntity(product);
    }

    public List<ProductResponse> getProductByCategory(Long categoryId) {

        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Category ID không hợp lệ");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        List<Product> products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(ProductResponse.fromEntity(product));
        }
        return productResponses;
    }

    public List<ProductResponse> searchProducts(String keyword) {
        List<Product> products = productRepository.searchActiveProductsNoPage(keyword);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(ProductResponse.fromEntity(product));
        }
        return productResponses;
    }

    public ProductResponse createProduct(ProductRequest productRequest) {

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(
                        () -> new RuntimeException("Category not found with id " + productRequest.getCategoryId()));

        // Kiểm tra danh mục có đang hoạt động không
        if (!category.getIsActive()) {
            throw new RuntimeException("Danh mục chưa được kích hoạt");
        }

        // Kiểm tra tên sản phẩm
        if (productRequest.getName() == null || productRequest.getName().isBlank()) {
            throw new RuntimeException("Tên sản phẩm không được để trống");
        }

        // Kiểm tra giá sản phẩm
        if (productRequest.getPrice() == null || productRequest.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Giá sản phẩm phải lớn hơn 0");
        }

        // Kiểm tra số lượng sản phẩm
        if (productRequest.getQuantity() == null || productRequest.getQuantity() < 0) {
            throw new RuntimeException("Số lượng sản phẩm phải >= 0");
        }

        // Tạo đối tượng sản phẩm
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .lowStockThreshold(productRequest.getLowStockThreshold())
                .imageUrl(productRequest.getImageUrl())
                .category(category)
                // .specifications(productRequest.getSpecifications())
                .isActive(true)
                .build();
        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    public ProductResponse updatedProduct(Long id, Product product) {

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

        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0) {
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

        Product updateProduct = productRepository.save(currenProduct);
        return ProductResponse.fromEntity(updateProduct);
    }

    public ProductResponse deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id" + id));
        product.setIsActive(false);
        Product deleteProduct = productRepository.save(product);
        return ProductResponse.fromEntity(deleteProduct);
    }
}
