package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Token;
import com.example.demo.entity.User;
import com.example.demo.repository.TokenRepository;
import com.example.demo.util.JwtUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public String generateToken(User user) {
        log.info("Generating JWT token for user: {}", user.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        String jwtToken = jwtUtils.generateToken(userDetails);

        saveUserToken(user, jwtToken);

        log.info("JWT token generated and saved for user: {}", user.getUsername());
        return jwtToken;
    }

    public String generateRefreshToken(User user) {
        log.info("Generating refresh token for user: {}", user.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        log.info("Refresh token generated for user: {}", user.getUsername());
        return refreshToken;
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(String token, User user) {
        log.debug("Validating JWT token for user: {}", user.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        boolean isValid = jwtUtils.isTokenValid(token, userDetails);

        if (isValid) {
            isValid = !isTokenRevoked(token);
        }

        log.debug("Token validation result for user {}: {}", user.getUsername(), isValid);
        return isValid;
    }

    @Transactional(readOnly = true)
    public String extractUsername(String token) {
        log.debug("Extracting username from JWT token");
        return jwtUtils.extractUsername(token);
    }

    public void saveUserToken(User user, String jwtToken) {
        log.info("Saving JWT token to database for user: {}", user.getUsername());

        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(jwtUtils.getJwtExpirationInSeconds());

        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType("ACCESS_TOKEN")
                .expired(false)
                .revoked(false)
                .expirationDate(expirationDate)
                .build();

        tokenRepository.save(token);
        log.info("JWT token saved to database for user: {}", user.getUsername());
    }

    public void revokeAllUserTokens(User user) {
        log.info("Revoking all tokens for user: {}", user.getUsername());
        List<Token> validUserTokens = getValidUserTokens(user);
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
        log.info("All tokens revoked for user: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public List<Token> getValidUserTokens(User user) {
        return tokenRepository.findAllValidTokensByUser(user);
    }

    public String refreshToken(String refreshToken) {
        log.info("Refreshing JWT token");

        try {
            String username = jwtUtils.extractUsername(refreshToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.isTokenValid(refreshToken, userDetails)) {

                String newAccessToken = jwtUtils.generateToken(userDetails);
                log.info("New access token generated for user: {}", username);
                return newAccessToken;
            } else {
                log.warn("Invalid refresh token provided");
                throw new RuntimeException("Invalid refresh token");
            }
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new RuntimeException("Failed to refresh token", e);
        }
    }

    public void logout(String token) {
        log.info("Logging out - revoking token");
        tokenRepository.findByToken(token).ifPresent(storedToken -> {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            log.info("Token revoked successfully");
        });
    }

    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired tokens");
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired tokens cleaned up");
    }

    @Transactional(readOnly = true)
    public boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired using JwtUtils");
        return jwtUtils.isTokenExpired(token);
    }

    @Transactional(readOnly = true)
    public boolean isTokenRevoked(String token) {
        return tokenRepository.findByToken(token)
                .map(Token::getRevoked)
                .orElse(true);
    }
}
