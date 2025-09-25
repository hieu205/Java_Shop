package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.CartItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Fallback;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    public Cart getUserCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart;
    }

    public Cart addItemToCart(Long userId, CartItem cartItem) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("product not found with id " + cartItem.getProduct().getId()));

        if (product.getIsActive() == false) {
            throw new RuntimeException("product is not active");
        }

        // check so luong hang san co va hang trong don
        if (product.getQuantity() < cartItem.getQuantity()) {
            throw new RuntimeException("khong du hang voi san pham: " + product.getName());
        }

        // kiem tra coi san pham da co trong gio hang chua
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem cart_Item = existingItem.get();
            int newQuantity = cart_Item.getQuantity() + cartItem.getQuantity();

            if (product.getQuantity() < newQuantity) {
                throw new RuntimeException("het hang/khong du hang");
            }

            cart_Item.setQuantity(newQuantity);
            cartItemRepository.save(cart_Item);
        } else {

            // Thêm sản phẩm mới vào giỏ hàng
            CartItem newItem = new CartItem();
            newItem.setCart(cart); // gán giỏ hàng hiện tại
            newItem.setProduct(product); // gán sản phẩm
            newItem.setQuantity(cartItem.getQuantity()); // gán số lượng

            // thêm vào list để duy trì quan hệ 2 chiều
            cart.getItems().add(newItem);

            cartItemRepository.save(newItem);
        }
        cartRepository.save(cart);
        return cart;
    }

    public Cart updateQuantityCartItem(Long userId, Long itemId, Integer quantity) {

        // da check ngoai le o ham getOrCreateCart
        Cart cart = getOrCreateCart(userId);

        if (quantity == null) {
            throw new RuntimeException("so luong khong duoc de trong");
        }

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart Item not found with id " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("cart item khong thuoc user");
        }

        if (quantity <= 0) {
            throw new RuntimeException("So luong khong duoc <= 0");
        }

        Product product = cartItem.getProduct();
        if (product.getIsActive() == false) {
            throw new RuntimeException("san pham chua duoc Active");
        }

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("het hang/ khong du hang");

        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cart;
    }

    public String removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart Item not found with id " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("cart item khong thuoc user");
        }
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
        return "xoa item thanh cong";
    }

    public String clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(cartItems);
        cartRepository.save(cart);
        return "xoa tat cac don hang trong Cart thanh cong";
    }
}
