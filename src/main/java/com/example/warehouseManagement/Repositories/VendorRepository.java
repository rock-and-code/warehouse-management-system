package com.example.warehouseManagement.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.Vendor;

public interface VendorRepository extends CrudRepository<Vendor, Long>{
    
}
