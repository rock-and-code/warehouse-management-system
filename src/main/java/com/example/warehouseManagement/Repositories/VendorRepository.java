package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.warehouseManagement.Domains.Vendor;

public interface VendorRepository
        extends JpaRepository<Vendor, Long>,
                JpaSpecificationExecutor<Vendor> {

    /**
     * Case-insensitive LIKE search on name, capped by Pageable.
     * Used by the global search bar (mode = CONTAINS).
     */
    List<Vendor> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Case-insensitive prefix match on name (mode = PREFIX in /search).
     */
    List<Vendor> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);
}
