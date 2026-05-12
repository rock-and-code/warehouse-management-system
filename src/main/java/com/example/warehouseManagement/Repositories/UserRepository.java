package com.example.warehouseManagement.Repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken(String token);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
