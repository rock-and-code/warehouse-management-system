package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;


public interface SalesOrderDto {
    Long getId();
    Integer getStatus();
    LocalDate getDate();
    Integer getSalesOrder();
    Double getTotal();
}
