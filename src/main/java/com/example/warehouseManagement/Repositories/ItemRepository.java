package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;


public interface ItemRepository extends JpaRepository<Item, Long>{

    /**
     * Returns a list of all the products by a vendor
     * @param vendor
     * @return
     */
    List<Item> findByVendor(Vendor vendor);
    
}
