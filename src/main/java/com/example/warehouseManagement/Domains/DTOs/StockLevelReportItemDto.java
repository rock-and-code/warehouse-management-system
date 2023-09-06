package com.example.warehouseManagement.Domains.DTOs;

public interface StockLevelReportItemDto {
    Integer getVendorId();
    Integer getSku();
    String getDescription();
    Integer getAverageWeekSales();
    Integer getQtyOnHand();
    Double getQtyOnHandWOI();
    Integer getQtyInTransit();
    Double getQtyInTransitWOI();
}
