package com.example.warehouseManagement.Domains.DTOs;

public interface InventorySnapshotDto {
    Double getTotalValue();
    Long getTotalUnits();
    Long getOosCount();
    Long getLowStockCount();
}
