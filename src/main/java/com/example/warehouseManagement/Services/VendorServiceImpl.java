package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Repositories.VendorRepository;

@Service
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;
    
    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * Returns a list of all the vendors persisted in the dba
     */
    @Override
    public Iterable<Vendor> findAll() {
        return vendorRepository.findAll();
    }

    /**
     * Returns a vendor by a given id
     */
    @Override
    public Optional<Vendor> findById(Long id) {
        return vendorRepository.findById(id);
    }

    /**
     * Persists a given vendor in the dba
     */
    @Override
    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    /**
     * Deletes a vendor from the dba
     */
    @Override
    public void delete(Vendor vendor) {
        vendorRepository.delete(vendor);
    }
    
}
