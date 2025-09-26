package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/v1/users/register").permitAll()
                                                .anyRequest().authenticated() // yêu cầu login
                                )
                                .httpBasic(Customizer.withDefaults()); // dùng Basic Auth

                return http.build();
        }

        @Bean
        public UserDetailsService users() {
                UserDetails admin = User.withUsername("admin")
                                .password("{noop}123") // {noop} = không mã hoá
                                .roles("ADMIN")
                                .build();

                UserDetails staff = User.withUsername("staff")
                                .password("{noop}123")
                                .roles("STAFF")
                                .build();

                UserDetails user = User.withUsername("user")
                                .password("{noop}123")
                                .roles("USER")
                                .build();

                return new InMemoryUserDetailsManager(admin, staff, user);
        }
}
