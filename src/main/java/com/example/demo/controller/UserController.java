package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.UserService;
import com.example.demo.entity.User;

@RestController
@RequestMapping("/api/v1/users")

public class UserController {
    @Autowired
    private UserService userService;

    // CREATE USER
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User saveUser = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveUser);
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // UPDATE USER BY ID
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable Long id, @RequestBody User newUser) {
        return ResponseEntity.ok(userService.updateUserById(id, newUser));
    }

    // Delete User By Id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Xoa nguoi dung thanh cong");
    }

}
