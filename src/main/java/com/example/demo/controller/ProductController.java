package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // // create product
    // @PostMapping("/register")
    // public ResponseEntity<Product> register(@RequestBody Product product) {
    // return ResponseEntity.ok(productService.register(product));
    // }

    // // get product by Id
    // @GetMapping("/{id}")
    // public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    // Product product = productService.getProductById(id);
    // return ResponseEntity.ok(product);
    // }

    // // get all products
    // @GetMapping
    // public ResponseEntity<List<Product>> getAllProducts() {
    // List<Product> products = productService.getAllProducts();
    // return ResponseEntity.ok(products);
    // }

    // // update product by Id
    // @PutMapping("/{id}")
    // public ResponseEntity<Product> updateProductById(@PathVariable Long id,
    // @RequestBody Product product) {
    // Product updatedProduct = productService.updateProductById(id, product);
    // return ResponseEntity.ok(updatedProduct);
    // }

    // // delete product by Id
    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteProductById(@PathVariable Long id) {
    // productService.deleteProductById(id);
    // return ResponseEntity.ok("Product with id " + id + " has been deleted
    // successfully.");
    // }

    // lay tat ca san pham da Active ve
    @GetMapping()
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // lay chi tiet san pham theo id
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // lay san pham theo danh muc
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductByCategory(categoryId));
    }

    // tim kiem san pham
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    // tao san pham moi (chi co Admin or Staff)
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping()
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    // cap nhat san pham (chi co Admin or Staff)
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updatedProduct(@PathVariable Long id, @RequestParam Product product) {
        return ResponseEntity.ok(productService.updatedProduct(id, product));
    }

    // xoa Product (chi co Admin or Staff)
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}
