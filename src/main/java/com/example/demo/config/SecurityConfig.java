
package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
        private final CustomUserDetailsService customUserDetailsService;
        private final UserDetailsService userDetailsService;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                log.info("Configuring Security Filter Chain");

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors().disable()
                                .authorizeHttpRequests(auth -> auth
                                                // ==== PUBLIC ENDPOINTS ====
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users/refresh-token")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/users/check/**").permitAll()

                                                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/promotions/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()

                                                .requestMatchers("/h2-console/**", "/actuator/**", "/error", "/")
                                                .permitAll()

                                                // ==== ADMIN ONLY ====
                                                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/v1/users/role/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users/create")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasRole("ADMIN")
                                                .requestMatchers("/api/v1/roles/**").hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/v1/products/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/v1/categories/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/v1/promotions/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/promotions/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/promotions/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers("/api/v1/inventory/products/*/adjust").hasRole("ADMIN")
                                                .requestMatchers("/api/v1/inventory/products/*/threshold")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/api/v1/inventory/products/*/reserve")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/api/v1/inventory/products/*/release")
                                                .hasRole("ADMIN")

                                                // ==== STAFF & ADMIN ====
                                                .requestMatchers("/api/v1/inventory/**").hasAnyRole("STAFF", "ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/comments/*/reply")
                                                .hasAnyRole("STAFF", "ADMIN")
                                                .requestMatchers("/api/v1/orders/*/status").hasAnyRole("STAFF", "ADMIN")

                                                // ==== CUSTOMER, STAFF, ADMIN ====
                                                .requestMatchers("/api/v1/cart/**")
                                                .hasAnyRole("CUSTOMER", "STAFF", "ADMIN")
                                                .requestMatchers("/api/v1/orders/**")
                                                .hasAnyRole("CUSTOMER", "STAFF", "ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/products/*/comments")
                                                .hasAnyRole("CUSTOMER", "STAFF", "ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/comments/**")
                                                .hasAnyRole("CUSTOMER", "STAFF", "ADMIN")

                                                // ==== DEFAULT: AUTH REQUIRED ====
                                                .anyRequest().authenticated())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authenticationProvider(authenticationProvider())

                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                                .logout(logout -> logout
                                                .logoutUrl("/api/v1/users/logout")
                                                .logoutSuccessHandler((request, response,
                                                                authentication) -> SecurityContextHolder
                                                                                .clearContext()));

                http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

                log.info("Security Filter Chain configured successfully");
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(12);
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

}
