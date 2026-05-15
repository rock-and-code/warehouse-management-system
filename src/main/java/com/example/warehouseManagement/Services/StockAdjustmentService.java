package com.example.warehouseManagement.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.warehouseManagement.Domains.StockAdjustment;

public interface StockAdjustmentService {

    /**
     * Persist the adjustment record and apply its effect to the Stock table
     * in one transactional operation.
     */
    StockAdjustment apply(StockAdjustment adjustment);

    Page<StockAdjustment> findAll(Pageable pageable);
}
