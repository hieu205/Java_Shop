package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;

import com.example.demo.dto.response.AuthResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")

public class UserController {
    @Autowired
    private UserService userService;
    // ==================== JWT AUTHENTICATION ENDPOINTS ====================

    // đăng nhâp ng dung
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(userService.checkLogin(username, password));
    }

    // dang ki ng dung
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User newuser) {
        AuthResponse authResponse = userService.register(newuser);
        return ResponseEntity.ok(authResponse);
    }

    // ==================== USER PROFILE ENDPOINTS =======================

    // lay thong tin profile cua user hien tai
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@RequestParam String username) {
        UserResponse currentUser = userService.getCurrentUserProfile(username);
        return ResponseEntity.ok(currentUser);
    }

    // cap nhat thong tin cho profile hien tai
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(@PathVariable String username,
            @Valid @RequestBody User updateUser) {
        return ResponseEntity.ok(userService.updateCurrentUserProfile(username, updateUser));
    }

    // ==================== USER MANAGEMENT ENDPOINTS ====================

    // tao user moi (chi danh cho Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody User newUser) {
        return ResponseEntity.ok(userService.createUser(newUser));
    }

    // lay ve tat ca nguoi dung (chi danh cho Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    // lay chi tiet nguoi dung theo ID (chi danh cho Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Update thong tin nguoi dung (chi danh cho Admin)
     * chi cap nhat: email, password, fullname, phone, address
     * khong duoc thay doi: username, role
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable Long id, @Valid @RequestBody User newUser) {
        return ResponseEntity.ok(userService.updateUserById(id, newUser));
    }

    // delete user by id (chi danh cho Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Xoa nguoi dung thanh cong");
    }

    // ==================== USER UTILITIES ENDPOINTS ====================

    // lay thong tin nguoi dung theo username
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // lay danh sach user theo role
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUserByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUserByRole(role));
    }

    // check username da ton tai
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        return ResponseEntity.ok(userService.checkUsernameExists(username));
    }

    // check email da ton tai hay chua
    @GetMapping("check/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        return ResponseEntity.ok(userService.checkEmailExists(email));
    }
}
