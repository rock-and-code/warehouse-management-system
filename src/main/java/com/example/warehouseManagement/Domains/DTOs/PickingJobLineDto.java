package com.example.warehouseManagement.Domains.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PickingJobLineDto {
    @Builder.Default
    private Long wareHouseSectionId = 0L;
    @Builder.Default
    private int qtyPicked = 0;
}
