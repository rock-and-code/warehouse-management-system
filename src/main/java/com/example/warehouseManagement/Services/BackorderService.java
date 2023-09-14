package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.SalesOrder;

public interface BackorderService {
    public Iterable<Backorder> findAll();
    public List<Backorder> findBySalesOrder(SalesOrder salesOrder);
    public Backorder save(Backorder backorder);
    public void delete(Backorder backorder);
}
