package com.example.warehouseManagement.Repositories;

import org.springframework.data.repository.CrudRepository;
import com.example.warehouseManagement.Domains.Warehouse;

public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    
}
