package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.OrderRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new RuntimeException("gion hang rong");
        }
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            orderResponses.add(OrderResponse.fromEntity(order));
        }
        return orderResponses;
    }

    public List<OrderResponse> getOrderByUserId(Long id) {
        List<Order> orders = orderRepository.findOrderByUserId(id);
        if (orders.isEmpty()) {
            throw new RuntimeException("gio hang rong");
        }
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            orderResponses.add(OrderResponse.fromEntity(order));
        }
        return orderResponses;
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        return OrderResponse.fromEntity(order);
    }

    public OrderResponse getOrderByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found with code " + orderCode));
        return OrderResponse.fromEntity(order);

    }

    public List<OrderResponse> getOrderByStatus(String status) {
        List<Order> orders = orderRepository.findOrderByStatus(status);
        if (orders.isEmpty()) {
            throw new RuntimeException("khong co don hang nao co trang thai: " + status);
        }
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            orderResponses.add(OrderResponse.fromEntity(order));
        }
        return orderResponses;
    }

    public OrderResponse createOrderByCart(Long userId, OrderRequest newOrder) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found with userId" + userId));
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setFinalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentMethod(Order.PaymentMethod.COD);
        order.setShippingAddress(newOrder.getShippingAddress());
        order.setNotes(newOrder.getNotes());

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getIsActive() == false) {
                throw new RuntimeException("San pham chua Active");
            }

            // Kiểm tra tồn kho
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("san pham da het hoac khong du " + product.getName());
            }

            // Giảm tồn kho
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(cartItems);

        return OrderResponse.fromEntity(savedOrder);
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        Order.OrderStatus orderStatus;
        try {
            orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        order.setStatus(orderStatus);
        Order updateOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(updateOrder);
    }

    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id" + id));
        if (order.getStatus().equals(Order.OrderStatus.DELIVERED)) {
            throw new RuntimeException("Don hang dang duoc van chuyen den khong the huy");
        }

        if (order.getStatus().equals(Order.OrderStatus.CANCELLED)) {
            throw new RuntimeException("Don hang da duoc huy");
        }

        // Hoàn lại tồn kho
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);

        Order cancelledOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(cancelledOrder);
    }
}
