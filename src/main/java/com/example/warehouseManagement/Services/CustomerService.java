package com.example.warehouseManagement.Services;

import java.util.Optional;

import com.example.warehouseManagement.Domains.Customer;

public interface CustomerService {
    public Iterable<Customer> findAll();
    public Optional<Customer> findById(Long id);
    public Customer save(Customer customer);
    public void delete(Customer customer);
}
