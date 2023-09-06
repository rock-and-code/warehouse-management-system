package com.example.warehouseManagement.Domains.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoDto {
    private String description;
    private double salesPrice;
    private double cost;
    private int sku;
}
