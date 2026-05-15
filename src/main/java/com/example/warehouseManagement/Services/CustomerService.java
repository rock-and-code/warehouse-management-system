package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.DTOs.AdvancedCustomerSearchCriteria;
import com.example.warehouseManagement.Domains.Exceptions.CustomerNotFoundException;

public interface CustomerService {
    public Iterable<Customer> findAll();
    public Page<Customer> findAll(Pageable pageable);
    /**
     * Advanced search — empty criteria returns every row (matches Page<Customer>
     * shape from findAll). See AdvancedCustomerSearchCriteria.isActive() for
     * "did the user filter?" semantics.
     */
    public Page<Customer> findAdvanced(AdvancedCustomerSearchCriteria criteria, Pageable pageable);
    public Optional<Customer> findById(Long id);
    public Customer updateById(Long id, Customer customer) throws CustomerNotFoundException;
    public Customer save(Customer customer);
    public void delete(Customer customer);
}
