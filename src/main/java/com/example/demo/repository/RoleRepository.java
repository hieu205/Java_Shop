package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Tìm role theo tên
     */
    Optional<Role> findByName(String name);

    /**
     * Kiểm tra role có tồn tại theo tên
     */
    boolean existsByName(String name);
}
