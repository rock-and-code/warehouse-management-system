package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.warehouseManagement.Domains.User;
import com.example.warehouseManagement.Domains.DTOs.UserFormDto;

public interface UserService extends UserDetailsService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User create(UserFormDto dto);

    User update(Long id, UserFormDto dto);

    void delete(Long id);

    /** Generates a fresh 6-digit 2FA code, persists it with expiry, and returns it. */
    String generateTwoFactorCode(User user);

    /** Returns true if the code is valid and not expired. Always clears the code after a check. */
    boolean verifyTwoFactorCode(User user, String code);

    /** Generates a fresh password reset token (URL-safe), persists it with expiry, and returns it. */
    String generatePasswordResetToken(User user);

    /** Looks up a user by reset token; returns empty if not found or expired. */
    Optional<User> findByValidResetToken(String token);

    /** Sets a new password (BCrypt-hashed) and invalidates the reset token. */
    void resetPassword(User user, String rawPassword);

    void recordSuccessfulLogin(User user);
}
