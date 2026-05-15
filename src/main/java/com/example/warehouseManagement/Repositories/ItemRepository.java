package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;


public interface ItemRepository
        extends JpaRepository<Item, Long>,
                JpaSpecificationExecutor<Item> {

    /**
     * Returns a list of all the products by a vendor
     * @param vendor
     * @return
     */
    List<Item> findByVendor(Vendor vendor);

    /**
     * Case-insensitive LIKE search on description, capped by Pageable.
     * Used by the global search bar (mode = CONTAINS).
     */
    List<Item> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * Case-insensitive prefix match on description (mode = PREFIX in /search).
     */
    List<Item> findByDescriptionStartingWithIgnoreCase(String prefix, Pageable pageable);
}
