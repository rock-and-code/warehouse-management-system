package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Repositories.ItemRepository;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository productRepository;

    public ItemServiceImpl(ItemRepository productRepository) {
        this.productRepository = productRepository;
    }
    /**
     * Returns an interable of the list of all the products in the dba
     * @return
     */
    @Override
    public Iterable<Item> findAll() {
        return productRepository.findAll();
    }
    /**
     * Return a product given its id
     * @param id
     * @return
     */
    @Override
    public Optional<Item> findById(Long id) {
        return productRepository.findById(id);
    }
    /**
     * Return a list of products by a given vendor id
     * @param vendor
     * @return
     */
    @Override
    public List<Item> findByVendor(Vendor vendor) {
        return productRepository.findByVendor(vendor);
    }
    /**
     * Saves a new product in the dba
     * @param product
     * @return
     */
    @Override
    public Item save(Item product) {
        return productRepository.save(product);
    }
    /**
     * Deletes a product from the dba
     * @param product
     */
    @Override
    public void delete(Item product) {
        productRepository.delete(product);
    }
    
}
