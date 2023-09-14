package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

public interface InvoiceDto {
    Long getId();

    LocalDate getDate();

    Integer getSalesOrder();

    Double getTotal();
}
