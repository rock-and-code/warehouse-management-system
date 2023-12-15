package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCostDto {
    private Long itemId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double cost;
}

