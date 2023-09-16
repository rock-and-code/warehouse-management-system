package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.DTOs.BackorderDto;

public interface BackorderService {
    public Iterable<Backorder> findAll();
    public List<Backorder> findBySalesOrder(SalesOrder salesOrder);
    public Backorder save(Backorder backorder);
    public void delete(Backorder backorder);
    public List<BackorderDto> findBackordersByYear(int year);
}
