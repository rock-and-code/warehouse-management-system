package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

public interface PurchaseOrderDto {
    Long getId();
    LocalDate getDate();
    Integer getPurchaseOrder();
    Double getTotal();
}
