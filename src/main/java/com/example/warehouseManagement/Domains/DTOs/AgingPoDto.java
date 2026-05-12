package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

public interface AgingPoDto {
    Long getId();
    String getVendorName();
    LocalDate getDate();
    Integer getAgeDays();
    Double getTotal();
    Integer getStatus();
}
