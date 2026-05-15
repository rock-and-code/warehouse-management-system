package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.warehouseManagement.Domains.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

    /**
     * Case-insensitive LIKE search on name, capped by Pageable.
     * Used by the global search bar (mode = CONTAINS).
     */
    List<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Case-insensitive prefix match on name (mode = PREFIX in /search).
     */
    List<Customer> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);
}
