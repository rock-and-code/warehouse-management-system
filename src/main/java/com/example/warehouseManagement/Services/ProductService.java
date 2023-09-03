package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.Product;
import com.example.warehouseManagement.Domains.Vendor;

public interface ProductService {
    /**
     * Returns an interable of the list of all the products in the dba
     * @return
     */
    public Iterable<Product> findAll();
    /**
     * Return a product given its id
     * @param id
     * @return
     */
    public Optional<Product> findById(Long id);
    /**
     * Return a list of products by a given vendor id
     * @param vendor
     * @return
     */
    public List<Product> findByVendor(Vendor vendor);
    /**
     * Saves a new product in the dba
     * @param product
     * @return
     */
    public Product save(Product product);
    /**
     * Deletes a product from the dba
     * @param product
     */
    public void delete(Product product);
}
