package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.SalesOrder;


public interface BackorderRepository extends CrudRepository<Backorder, Long> {
    
    public List<Backorder> findBySalesOrder(SalesOrder salesOrder);
}
