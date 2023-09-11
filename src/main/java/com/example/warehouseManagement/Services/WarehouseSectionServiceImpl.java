package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.WarehouseSectionDto;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

@Service
public class WarehouseSectionServiceImpl implements WarehouseSectionService {
    private final WarehouseSectionRepository warehouseSectionRepository;

    public WarehouseSectionServiceImpl(WarehouseSectionRepository warehouseSectionRepository) {
        this.warehouseSectionRepository = warehouseSectionRepository;
    }

    @Override
    public List<List<WarehouseSection>> findBySalesOrder(SalesOrder salesOrder) {
        List<List<WarehouseSection>> warehouseSectionList = new ArrayList<>();
        for (SalesOrderLine salesOrderLine : salesOrder.getSaleOrderLines())
            warehouseSectionList.add(warehouseSectionRepository.findByItemId(salesOrderLine.getItem().getId()));
        return warehouseSectionList;
    }

    @Override
    public List<WarehouseSectionDto> findWarehouseSectionSuggestionBySalesOrder(SalesOrder salesOrder) {
        List<WarehouseSectionDto> warehouseSectionList = new ArrayList<>();
        for (SalesOrderLine salesOrderLine : salesOrder.getSaleOrderLines())
            warehouseSectionList.add(warehouseSectionRepository.findSectionWithHighestQtyOnHandByItemId(salesOrderLine.getItem().getId()));
        return warehouseSectionList;
    }
    
}