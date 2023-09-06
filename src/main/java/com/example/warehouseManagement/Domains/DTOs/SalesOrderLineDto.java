package com.example.warehouseManagement.Domains.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderLineDto {
    private int qty;
    private Long itemId; 
}
