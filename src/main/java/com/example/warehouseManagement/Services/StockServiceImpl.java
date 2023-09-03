package com.example.warehouseManagement.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.DTOs.TopFiveMoversDto;
import com.example.warehouseManagement.Repositories.StockRepository;

@Service
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<TopFiveMoversDto> getTopFiveMovers() {
        return stockRepository.getTopFiveMovers();
    }
    
}
