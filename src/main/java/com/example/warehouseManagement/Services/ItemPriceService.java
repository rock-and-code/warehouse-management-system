package com.example.warehouseManagement.Services;

import com.example.warehouseManagement.Domains.ItemPrice;

public interface ItemPriceService {
    public double findCurrentPriceByItemId(Long itemId);
    public ItemPrice findCurrentItemPriceByItemId(Long itemId);
}
