package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

public interface DailySalesDto {
    LocalDate getDay();
    Double getTotal();
}
