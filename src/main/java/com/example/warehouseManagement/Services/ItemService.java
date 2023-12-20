package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.Exceptions.ItemNotFoundException;

public interface ItemService {
    /**
     * Returns an interable of the list of all the products in the dba
     * @return
     */
    public Iterable<Item> findAll();
    /**
     * Return a product given its id
     * @param id
     * @return
     */
    public Optional<Item> findById(Long id);
    /**
     * Return a list of products by a given vendor id
     * @param vendor
     * @return
     */
    public List<Item> findByVendor(Vendor vendor);
    /**
     * Updates an existing product in the dba by a given item id
     * @param id
     * @param item
     * @return
     */
    public Item updateDescriptionAndSKUById(Long id, Item item) throws ItemNotFoundException;
    /**
     * Saves a new product in the dba
     * @param item
     * @return
     */
    public Item save(Item item);
    /**
     * Deletes a product from the dba
     * @param item
     */
    public void delete(Item item);
}
