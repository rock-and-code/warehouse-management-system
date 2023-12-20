package com.example.warehouseManagement.Services;

import java.util.Optional;

import com.example.warehouseManagement.Domains.ItemPrice;
import com.example.warehouseManagement.Domains.Exceptions.ItemPriceNotFoundException;

public interface ItemPriceService {
    public Iterable<ItemPrice> findAll();
    public Optional<ItemPrice> findById(Long id);
    public ItemPrice updateEndDateById(Long id, ItemPrice itemPrice) throws ItemPriceNotFoundException;
    public ItemPrice save(ItemPrice itemPrice);
    public void delete(ItemPrice itemPrice);
    public double findCurrentPriceByItemId(Long itemId);
    public ItemPrice findCurrentItemPriceByItemId(Long itemId);
}
