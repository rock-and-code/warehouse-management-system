package com.example.warehouseManagement.Domains.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
public class WarehouseSectionIdDto {
    @Builder.Default
    private int warehouseSectionId = 0;
}
