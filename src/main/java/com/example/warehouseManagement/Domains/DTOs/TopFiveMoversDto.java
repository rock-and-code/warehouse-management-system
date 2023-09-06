package com.example.warehouseManagement.Domains.DTOs;

public interface TopFiveMoversDto {
    Integer getSku();
    String getDescription();
    Integer getQtyOnHand();
    Integer getAverageWeekSales();
    Double getWeeksOfInventory();
}
