package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.WarehouseSectionDto;

public interface WarehouseSectionService {
    public List<List<WarehouseSectionDto>> findBySalesOrder(SalesOrder salesOrder);
    public Optional<WarehouseSection> findWarehouseSectionBySectionNumber(String sectionNumber);
    public List<WarehouseSectionDto> findWarehouseSectionSuggestionBySalesOrder(SalesOrder salesOrder);
    public Iterable<WarehouseSection> findAll();
}
