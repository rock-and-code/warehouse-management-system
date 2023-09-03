package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;


public interface SalesOrderDto {
    Long getId();
    LocalDate getDate();
    Integer getSalesOrder();
    Double getTotal();
}
