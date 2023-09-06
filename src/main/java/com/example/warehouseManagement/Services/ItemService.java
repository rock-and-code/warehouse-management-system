package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;

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
     * Saves a new product in the dba
     * @param product
     * @return
     */
    public Item save(Item product);
    /**
     * Deletes a product from the dba
     * @param product
     */
    public void delete(Item product);
}
