package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.Exceptions.ItemNotFoundException;
import com.example.warehouseManagement.Repositories.ItemRepository;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    /**
     * Returns an interable of the list of all the items in the dba
     * @return
     */
    @Override
    public Iterable<Item> findAll() {
        return itemRepository.findAll();
    }
    /**
     * Return a item given its id
     * @param id
     * @return
     */
    @Override
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }
    /**
     * Return a list of items by a given vendor id
     * @param vendor
     * @return
     */
    @Override
    public List<Item> findByVendor(Vendor vendor) {
        return itemRepository.findByVendor(vendor);
    }
    /**
     * Updates an existing item in the dba by a given item id
     * @param id
     * @param item
     * @return
     */
    public Item updateDescriptionAndSKUById(Long id, Item item) throws ItemNotFoundException {
        if (itemRepository.findById(id).isEmpty()) {
            throw new ItemNotFoundException();
        } else {
            Item existing = itemRepository.findById(id).get();

            existing.setDescription(item.getDescription());
            existing.setSku(item.getSku());
            return itemRepository.save(existing);
        }
    }
    /**
     * Saves a new item in the dba
     * @param item
     * @return
     */
    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }
    /**
     * Deletes a item from the dba
     * @param item
     */
    @Override
    public void delete(Item item) {
        itemRepository.delete(item);
    }
    
}
