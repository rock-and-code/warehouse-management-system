package com.example.warehouseManagement.Services;

import com.example.warehouseManagement.Domains.ItemCost;

public interface ItemCostService {
    public double findCurrentCostByItemId(Long itemId);
    public ItemCost findCurrentItemCostByItemId(Long itemId);
}
