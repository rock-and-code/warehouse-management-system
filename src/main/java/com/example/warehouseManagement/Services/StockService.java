package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.DTOs.TopFiveMoversDto;

public interface StockService {
    /**
     * Get a list of the top five movers 
     */
    public List<TopFiveMoversDto> getTopFiveMovers();
}
