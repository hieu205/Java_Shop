package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // get cart by user
    public Cart getCartByUser(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    // add prodict to cart
    public Cart addProductToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUser(userId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setProductId(productId);
            item.setQuantity(quantity);
            item.setCart(cart);
            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }

    // Xóa product khỏi cart
    public Cart removeProductFromCart(Long userId, Long productId) {
        Cart cart = getCartByUser(userId);

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        return cartRepository.save(cart);
    }

    // Xóa toàn bộ cart
    public void clearCart(Long userId) {
        Cart cart = getCartByUser(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
