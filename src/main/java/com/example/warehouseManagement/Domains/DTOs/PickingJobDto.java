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
public class PickingJobDto {
    private Long id;
    @Builder.Default
    private LocalDate date = LocalDate.now();
    private Long salesOrderId;
    @Builder.Default
    private List<PickingJobLineDto> pickingJobDtoLines = new ArrayList<>();
}
