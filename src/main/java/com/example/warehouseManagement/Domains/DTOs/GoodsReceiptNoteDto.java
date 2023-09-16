package com.example.warehouseManagement.Domains.DTOs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GoodsReceiptNoteDto {
    private LocalDate date;
    private Long purchaseOrderId;
    @Builder.Default
    private List<GoodsReceiptNoteLineDto> goodsReceiptNoteLines = new ArrayList<>();
}
