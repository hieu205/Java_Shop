package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.response.AuthResponse;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    private AuthenticationManager authenticationManager;
    private TokenService tokenService;

    // check login
    public AuthResponse checkLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password));

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("Ten dang nhap hoac tai khoan khong dung");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        String accessToken = tokenService.generateToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(30L * 24 * 60 * 60)
                .user(UserResponse.fromEntity(user))
                .build();
    }

    public AuthResponse register(User user) {

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

        if (3 != user.getRole().getId()) {
            throw new IllegalArgumentException("Chỉ được phép đăng ký với role USER có id = 3");
        }
        User newUser = User.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .isActive(true)
                .build();
        User saveUser = userRepository.save(newUser);

        String accessToken = tokenService.generateToken(saveUser);
        String refreshToken = tokenService.generateRefreshToken(saveUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(30L * 24 * 60 * 60)
                .user(UserResponse.fromEntity(saveUser))
                .build();
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

        User user = User.builder()
                .username(newUser.getUsername())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .email(newUser.getEmail())
                .fullName(newUser.getFullName())
                .phone(newUser.getPhone())
                .address(newUser.getAddress())
                .role(newUser.getRole())
                .isActive(true)
                .build();
        User saveUser = userRepository.save(user);
        return UserResponse.fromEntity(saveUser);
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
