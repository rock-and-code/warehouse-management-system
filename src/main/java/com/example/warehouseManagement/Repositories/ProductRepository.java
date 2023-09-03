package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.Product;
import com.example.warehouseManagement.Domains.Vendor;


public interface ProductRepository extends CrudRepository<Product, Long>{

    /**
     * Returns a list of all the products by a vendor
     * @param vendor
     * @return
     */
    List<Product> findByVendor(Vendor vendor);
    
}
