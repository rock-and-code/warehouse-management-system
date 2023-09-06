package com.example.warehouseManagement.Services;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.ItemCost;
import com.example.warehouseManagement.Repositories.ItemCostRepository;

@Service
public class ItemCostServiceImpl implements ItemCostService {
    private final ItemCostRepository itemCostRepository;
    
    public ItemCostServiceImpl(ItemCostRepository itemCostRepository) {
        this.itemCostRepository = itemCostRepository;
    }

    @Override
    public double findCurrentCostByItemId(Long itemId) {
        return itemCostRepository.findCurrentCostByItemId(itemId);
    }

    @Override
    public ItemCost findCurrentItemCostByItemId(Long itemId) {
        return itemCostRepository.findCurrentItemCostByItemId(itemId);
    }
    
}
