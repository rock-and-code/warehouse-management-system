package com.example.warehouseManagement.Services;

import java.util.Optional;

import com.example.warehouseManagement.Domains.Invoice;
import com.example.warehouseManagement.Domains.PickingJob;

public interface InvoiceService {
    public Iterable<Invoice> findAll();
    public Optional<Invoice> findById(Long id);
    public Invoice save(Invoice invoice);
    public Invoice createByPickingJob(PickingJob pickingJob);
    public void delete(Invoice invoice);
}
