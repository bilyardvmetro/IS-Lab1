package com.islab1.services;

import com.islab1.entities.AuthToken;
import com.islab1.entities.User;
import com.islab1.entities.UserRole;
import com.islab1.repository.AuthTokenRepository;
import com.islab1.repository.UserRepository;
import com.islab1.utils.PasswordHasher;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Stateless
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthTokenRepository authTokenRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Регистрация нового пользователя.
     * Первый пользователь в системе получает роль ADMIN, остальные — USER.
     */
    @Transactional
    public User register(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Имя пользователя не может быть пустым.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BadRequestException("Пароль не может быть пустым.");
        }

        userRepository.findByUsername(username).ifPresent(u -> {
            throw new BadRequestException("Пользователь с таким именем уже существует.");
        });

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordHasher.hashPassword(rawPassword));

        // первый юзер — админ, остальные — обычные
        long count = userRepository.countAll();
        user.setRole(count == 0 ? UserRole.ADMIN : UserRole.USER);

        return userRepository.save(user);
    }

    /**
     * Аутентификация: проверка логина/пароля и выдача токена.
     */
    @Transactional
    public AuthToken login(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new NotAuthorizedException("Неверное имя пользователя или пароль.");
        }

        User user = userOpt.get();
        if (!PasswordHasher.verifyPassword(rawPassword, user.getPasswordHash())) {
            throw new NotAuthorizedException("Неверное имя пользователя или пароль.");
        }

        // Можно предварительно чистить устаревшие токены
        authTokenRepository.deleteExpired();

        AuthToken token = new AuthToken();
        token.setUser(user);
        token.setToken(generateTokenValue());

        return authTokenRepository.save(token);
    }

    public Optional<User> findUserByToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return Optional.empty();
        }

        return authTokenRepository.findByToken(tokenValue)
                .filter(t -> !t.isExpired())
                .map(AuthToken::getUser);
    }

    private String generateTokenValue() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
