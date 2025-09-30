package com.example.demo.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found");
        }

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        Optional<Role> optionalRole = roleRepository.findByName(user.getRole().getName());

        List<GrantedAuthority> authorities = new ArrayList<>();

        optionalRole.ifPresent(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
    }

}
