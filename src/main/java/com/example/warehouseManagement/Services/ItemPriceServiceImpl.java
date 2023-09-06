package com.example.warehouseManagement.Services;

import com.example.warehouseManagement.Domains.ItemPrice;
import com.example.warehouseManagement.Repositories.ItemPriceRepository;

public class ItemPriceServiceImpl implements ItemPriceService {
    private final ItemPriceRepository itemPriceRepository;
    
    public ItemPriceServiceImpl(ItemPriceRepository itemPriceRepository) {
        this.itemPriceRepository = itemPriceRepository;
    }

    @Override
    public double findCurrentPriceByItemId(Long itemId) {
        return itemPriceRepository.findCurrentPriceByItemId(itemId);
    }

    @Override
    public ItemPrice findCurrentItemPriceByItemId(Long itemId) {
        return itemPriceRepository.findCurrentItemPriceByItemId(itemId);
    }
    
}
