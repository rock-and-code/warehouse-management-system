package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.DTOs.StockLevelReportItemDto;
import com.example.warehouseManagement.Domains.DTOs.TopFiveMoversDto;

public interface StockService {
    /**
     * Get a list of the top five movers 
     */
    public List<TopFiveMoversDto> getTopFiveMovers();
    /**
     * Returns a list of stock level report rows. Each row contains vendor id,
     * item sku, item description, item average week sales, item's qty on hand, its weeks of inventory
     * the item's in trasit qty, and the item's in transit weeks of inventory
     * @param vendorId
     * @retur
     */
    public List<StockLevelReportItemDto> findStockReportsItemsByVendorId(Long vendorId);

    public void pickStock(PickingJob pickingJob);
}
