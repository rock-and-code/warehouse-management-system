package com.example.warehouseManagement.Domains.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutAwayTaskDto {
    @Builder.Default
    Long destinationWarehouseSectionId = 0L;
    @Builder.Default
    int qtyToMove = 0;
}
