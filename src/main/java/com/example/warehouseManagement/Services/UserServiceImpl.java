package com.example.warehouseManagement.Services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.User;
import com.example.warehouseManagement.Domains.DTOs.UserFormDto;
import com.example.warehouseManagement.Domains.Exceptions.DuplicateUserException;
import com.example.warehouseManagement.Domains.Exceptions.UserNotFoundException;
import com.example.warehouseManagement.Repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final int twoFactorTtlMinutes;
    private final int resetTokenTtlMinutes;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.auth.two-factor-code-ttl-minutes}") int twoFactorTtlMinutes,
                           @Value("${app.auth.password-reset-token-ttl-minutes}") int resetTokenTtlMinutes) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.twoFactorTtlMinutes = twoFactorTtlMinutes;
        this.resetTokenTtlMinutes = resetTokenTtlMinutes;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        UserBuilder builder = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .disabled(!user.isEnabled());
        return builder.build();
    }

    @Override
    public List<User> findAll() {
        List<User> all = new ArrayList<>();
        userRepository.findAll().forEach(all::add);
        return all;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email == null ? null : email.trim().toLowerCase());
    }

    @Override
    public User create(UserFormDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUserException("Username already in use");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException("Email already in use");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        User user = User.builder()
                .username(dto.getUsername().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .enabled(dto.isEnabled())
                .twoFactorEnabled(dto.isTwoFactorEnabled())
                .createdAt(Instant.now())
                .build();
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, UserFormDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));

        String newUsername = dto.getUsername().trim();
        String newEmail = dto.getEmail().trim().toLowerCase();

        if (!existing.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new DuplicateUserException("Username already in use");
        }
        if (!existing.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateUserException("Email already in use");
        }

        existing.setUsername(newUsername);
        existing.setEmail(newEmail);
        existing.setRole(dto.getRole());
        existing.setEnabled(dto.isEnabled());
        existing.setTwoFactorEnabled(dto.isTwoFactorEnabled());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return userRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public String generateTwoFactorCode(User user) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        user.setTwoFactorCode(passwordEncoder.encode(code));
        user.setTwoFactorCodeExpiresAt(Instant.now().plus(twoFactorTtlMinutes, ChronoUnit.MINUTES));
        userRepository.save(user);
        return code;
    }

    @Override
    public boolean verifyTwoFactorCode(User user, String code) {
        String stored = user.getTwoFactorCode();
        Instant expiry = user.getTwoFactorCodeExpiresAt();
        boolean valid = stored != null
                && expiry != null
                && expiry.isAfter(Instant.now())
                && passwordEncoder.matches(code, stored);
        // Single-use: always clear after a check
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpiresAt(null);
        userRepository.save(user);
        return valid;
    }

    @Override
    public String generatePasswordResetToken(User user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(Instant.now().plus(resetTokenTtlMinutes, ChronoUnit.MINUTES));
        userRepository.save(user);
        return token;
    }

    @Override
    public Optional<User> findByValidResetToken(String token) {
        return userRepository.findByPasswordResetToken(token)
                .filter(u -> u.getPasswordResetTokenExpiresAt() != null
                        && u.getPasswordResetTokenExpiresAt().isAfter(Instant.now()));
    }

    @Override
    public void resetPassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        userRepository.save(user);
    }

    @Override
    public void recordSuccessfulLogin(User user) {
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
    }
}
