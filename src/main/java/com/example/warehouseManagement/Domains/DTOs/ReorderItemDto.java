package com.example.warehouseManagement.Domains.DTOs;

public interface ReorderItemDto {
    Long getItemId();
    Integer getSku();
    String getDescription();
    String getVendorName();
    Integer getQtyOnHand();
    Integer getWeeklyVelocity();
    Double getWeeksOfInventory();
}
