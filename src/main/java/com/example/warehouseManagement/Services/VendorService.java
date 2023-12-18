package com.example.warehouseManagement.Services;

import java.util.Optional;

import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.Exceptions.VendorNotFoundException;

public interface VendorService {
    /**
     * Returns a list of all the vendors persisted in the dba
     * @return
     */
    public Iterable<Vendor> findAll();
    /**
     * Returns a vendor by a given id
     * @param id
     * @return
     */
    public Optional<Vendor> findById(Long id);
    public Vendor updateById(Long id, Vendor vendor) throws VendorNotFoundException;
    /**
     * Persist a given vendor in the dba
     * @param vendor
     * @return
     */
    public Vendor save(Vendor vendor);
    /**
     * Removes a given vendor from the dba
     * @param vendor
     */
    public void delete(Vendor vendor);
}

