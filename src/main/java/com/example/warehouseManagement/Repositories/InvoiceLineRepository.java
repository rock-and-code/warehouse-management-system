package com.example.warehouseManagement.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.InvoiceLine;

public interface InvoiceLineRepository extends CrudRepository<InvoiceLine, Long> {
    
}
