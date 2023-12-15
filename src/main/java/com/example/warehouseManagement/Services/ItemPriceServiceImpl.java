package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.ItemPrice;
import com.example.warehouseManagement.Repositories.ItemPriceRepository;

@Service
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

    @Override
    public Iterable<ItemPrice> findAll() {
        return itemPriceRepository.findAll();
    }

    @Override
    public Optional<ItemPrice> findById(Long id) {
        return itemPriceRepository.findById(id);
    }

    @Override
    public ItemPrice save(ItemPrice itemPrice) {
        return itemPriceRepository.save(itemPrice);
    }

    @Override
    public void delete(ItemPrice itemPrice) {
        itemPriceRepository.delete(itemPrice);
    }
    
}
