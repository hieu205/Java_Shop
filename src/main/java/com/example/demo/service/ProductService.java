package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // create product
    public Product register(Product product) {
        return productRepository.save(product);
    }

    // get product by Id
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product không tồn tại với id: " + id));
    }

    // get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // update product by Id
    public Product updateProductById(Long id, Product newProduct) {
        Product product = getProductById(id);

        if (newProduct.getName() != null && !newProduct.getName().isEmpty()) {
            product.setName(newProduct.getName());
        }

        if (newProduct.getDescription() != null) {
            product.setDescription(newProduct.getDescription());
        }

        if (newProduct.getPrice() != null && newProduct.getPrice() >= 0) {
            product.setPrice(newProduct.getPrice());
        }

        if (newProduct.getQuantity() != null && newProduct.getQuantity() >= 0) {
            product.setQuantity(newProduct.getQuantity());
        }

        if (newProduct.getLowStockThreshold() != null && newProduct.getLowStockThreshold() >= 0) {
            product.setLowStockThreshold(newProduct.getLowStockThreshold());
        }

        if (newProduct.getIsActive() != null) {
            product.setIsActive(newProduct.getIsActive());
        }

        if (newProduct.getImageUrl() != null) {
            product.setImageUrl(newProduct.getImageUrl());
        }

        return productRepository.save(product);
    }

    // delete product by Id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Product với id: " + id);
        }
        productRepository.deleteById(id);
    }

}
