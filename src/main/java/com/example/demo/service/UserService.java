package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // create user
    public User register(User user) {
        return userRepository.save(user);
    }

    // Lay thong tin ng dung theo ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại với id: " + id));
    }

    // Update User by Id
    public User updateUserById(Long id, User newUser) {
        User user = getUserById(id);

        if (newUser.getFullname() != null) {
            user.setFullname(newUser.getFullname());
        }
        if (newUser.getAddress() != null) {
            user.setAddress(newUser.getAddress());
        }
        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getPassword() != null) {
            user.setPassword(newUser.getPassword());
        }
        if (newUser.getPhone() != null) {
            user.setPhone(newUser.getPhone());
        }
        if (newUser.getRoleId() != null) {
            user.setRoleId(newUser.getRoleId());
        }
        if (newUser.getUsername() != null) {
            user.setUsername(newUser.getUsername());
        }

        return userRepository.save(user);
    }

    // delete User By Id
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}
