package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.TokenService;
import com.example.demo.util.JwtUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            username = jwtUtils.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (tokenService.isTokenRevoked(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtils.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT token is invalid for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/v1/users/register") ||
                requestURI.startsWith("/api/v1/users/login") ||
                requestURI.startsWith("/api/v1/users/check/") ||
                requestURI.startsWith("/api/v1/users/refresh-token") ||
                // requestURI.startsWith("/api/v1/products") ||
                // requestURI.startsWith("/api/v1/categories") ||
                // Comment read access
                requestURI.startsWith("/api/v1/products/") && requestURI.contains("/comments") ||
                requestURI.startsWith("/api/v1/comments/") ||
                // Promotion public endpoints
                requestURI.startsWith("/api/v1/promotions/active") ||
                requestURI.startsWith("/api/v1/promotions/applicable") ||
                requestURI.startsWith("/api/v1/promotions/best") ||
                requestURI.startsWith("/api/v1/promotions/") && requestURI.contains("/calculate-discount") ||
                requestURI.startsWith("/api/v1/promotions/") && requestURI.contains("/is-active") ||
                requestURI.startsWith("/api/v1/promotions/") && requestURI.contains("/is-applicable") ||
                // Development endpoints
                requestURI.startsWith("/h2-console") ||
                requestURI.startsWith("/actuator") ||
                requestURI.equals("/") ||
                requestURI.startsWith("/error");
    }
}
