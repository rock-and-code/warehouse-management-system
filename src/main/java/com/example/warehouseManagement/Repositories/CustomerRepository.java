package com.example.warehouseManagement.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long>{
    
}
