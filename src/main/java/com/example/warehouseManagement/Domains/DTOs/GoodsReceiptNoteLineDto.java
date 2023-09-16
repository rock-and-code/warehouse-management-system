package com.example.warehouseManagement.Domains.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GoodsReceiptNoteLineDto {
    @Builder.Default
    private int qty = 0;
    @Builder.Default
    private Long warehouseSectionId = 0L;
    @Builder.Default
    private int damaged = 0;
}
