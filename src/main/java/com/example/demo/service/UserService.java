package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // check login
    public boolean checkLogin(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("Ten dang nhap hoac tai khoan khong dung");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        if (password.equals(user.getPassword())) {
            return true;
        }

        return false;
    }

    public UserResponse register(User user) {

        // check cac gia tri null truoc khi xu li
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        // check username
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User name đã tồn tại");
        }

        // check email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    public UserResponse createUser(User newUser) {

        // check gia tri null truoc khi xu li
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        // check username
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new RuntimeException("Username da ton tai");
        }

        // check email
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("Email da ton tai");
        }

        userRepository.save((newUser));
        // UserResponse userResponse = new UserResponse();
        return UserResponse.fromEntity(newUser);
    }

    public List<UserResponse> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(UserResponse.fromEntity(user));
        }
        return userResponses;
    }

    public UserResponse getUserById(Long id) {
        User saveUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại với id: " + id));
        if (saveUser.getIsActive() == false) {
            throw new RuntimeException("Tai khoan da bi khoa ");
        }

        return UserResponse.fromEntity(saveUser);
        // return userResponse;
    }

    public UserResponse updateUserById(Long id, User newUser) {

        // da check ngoai le o ham getUserById
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found with id " + id));

        // Username khong duoc thay doi
        if (newUser.getUsername() != null && !user.getUsername().equals(newUser.getUsername())) {
            throw new RuntimeException("Username khong duoc thay doi");
        }

        // role khong duoc phep thay doi
        if (newUser.getRole() != null) {
            throw new RuntimeException("Role khong duoc phep thay doi qua endpoint nay");
        }

        // cập nhật fullname
        if (newUser.getFullName() != null) {
            user.setFullName(newUser.getFullName());
        }
        // cập nhật địa chỉ
        if (newUser.getAddress() != null) {
            user.setAddress(newUser.getAddress());
        }
        // cập nhật email
        if (newUser.getEmail() != null && !user.getEmail().equals(newUser.getEmail())
                && userRepository.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("Email da ton tai");

        }
        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }
        // cập nhật password
        if (newUser.getPassword() != null) {
            user.setPassword(newUser.getPassword());
        }
        // cập nhật số điện thoại
        if (newUser.getPhone() != null) {
            user.setPhone(newUser.getPhone());
        }

        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    public void deleteUserById(Long id) {
        User saveUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        saveUser.setIsActive(false);

        userRepository.save(saveUser);
    }

    public UserResponse getCurrentUserProfile(String username) {
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Username not found with Username" + username);
        }

        return UserResponse.fromEntity(currentUser);

    }

    public UserResponse updateCurrentUserProfile(String username, User updateUser) {

        // check username
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Username không tồn tại");
        }

        // check email
        if (!currentUser.getEmail().equals(updateUser.getEmail())
                && userRepository.existsByEmail(updateUser.getEmail())) {
            throw new RuntimeException("Email da ton tai");
        }
        currentUser.setEmail(updateUser.getEmail());
        currentUser.setFullName(updateUser.getFullName());
        currentUser.setPhone(updateUser.getPhone());
        currentUser.setAddress(updateUser.getAddress());
        userRepository.save(currentUser);

        return UserResponse.fromEntity(currentUser);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Username khong ton tai");
        }
        return UserResponse.fromEntity(user);
    }

    public List<UserResponse> getUserByRole(String role) {
        Role entityRole = roleRepository.findByName(role.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found " + role));
        List<User> users = userRepository.findByRole(entityRole);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(UserResponse.fromEntity(user));
        }
        return userResponses;
    }

    public Boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public Boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
