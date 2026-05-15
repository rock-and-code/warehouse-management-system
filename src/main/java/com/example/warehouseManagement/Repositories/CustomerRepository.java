package com.example.warehouseManagement.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.warehouseManagement.Domains.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

}
