package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrderByUserId(Long id);

    Optional<Order> findByOrderCode(String orderCode);

    // @Query(value = "SELECT * FROM orders WHERE user_id = :userId AND status =
    // :status ORDER BY created_at DESC LIMIT :offset, :limit", nativeQuery = true)
    // List<Order> findByUserIdAndStatusNative(
    // @Param("userId") Long userId,
    // @Param("status") String status,
    // @Param("offset") int offset,
    // @Param("limit") int limit);

    List<Order> findOrderByStatus(String status);
}
