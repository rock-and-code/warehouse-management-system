package com.example.warehouseManagement.Domains.DTOs;

public interface PendingSalesOrderDto {
    //LocalDate getDate();
    String getDate();
    String getCustomer();
    Integer getSalesOrder();
    Double getTotal();
    Integer getDelay();
}
