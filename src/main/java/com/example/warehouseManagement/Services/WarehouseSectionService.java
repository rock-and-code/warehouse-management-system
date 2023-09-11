package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.WarehouseSectionDto;

public interface WarehouseSectionService {
    public List<List<WarehouseSection>> findBySalesOrder(SalesOrder salesOrder);
    public List<WarehouseSectionDto> findWarehouseSectionSuggestionBySalesOrder(SalesOrder salesOrder);
}
