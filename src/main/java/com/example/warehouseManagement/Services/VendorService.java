package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Domains.DTOs.AdvancedVendorSearchCriteria;
import com.example.warehouseManagement.Domains.Exceptions.VendorNotFoundException;

public interface VendorService {
    /**
     * Iterable variant — keep for callers populating <select> dropdowns in
     * other forms (NewItemForm, NewPurchaseOrderForm). Don't remove.
     */
    public Iterable<Vendor> findAll();
    /**
     * Paginated variant used by the /vendors list page.
     */
    public Page<Vendor> findAll(Pageable pageable);
    /**
     * Advanced search — empty criteria returns every row (matches Page<Vendor>
     * shape from findAll). Spec returns cb.conjunction() when nothing is set.
     */
    public Page<Vendor> findAdvanced(AdvancedVendorSearchCriteria criteria, Pageable pageable);
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

