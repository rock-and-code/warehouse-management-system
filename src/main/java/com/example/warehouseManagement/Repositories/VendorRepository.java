package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.Vendor;

public interface VendorRepository extends CrudRepository<Vendor, Long>{

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
