package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

public interface BackorderDto {
    LocalDate getDate();
    String getCustomer();
    Long getSalesOrder();
    Double getTotal();
}
