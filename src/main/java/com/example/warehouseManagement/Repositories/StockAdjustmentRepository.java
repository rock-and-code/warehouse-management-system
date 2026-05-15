package com.example.warehouseManagement.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.warehouseManagement.Domains.StockAdjustment;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
}
