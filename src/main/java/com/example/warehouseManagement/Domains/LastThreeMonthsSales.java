package com.example.warehouseManagement.Domains;

import java.util.ArrayList;
import java.util.List;

import com.example.warehouseManagement.Domains.DTOs.MonthlySalesDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LastThreeMonthsSales {
    @Builder.Default
    private List<MonthlySalesDto> lastThreeMonthsSales = new ArrayList<>();
    public double getThreeMonthsAverageSales() {
        double sum = 0.0;
        for (MonthlySalesDto monthlySalesDto : lastThreeMonthsSales)
            sum += monthlySalesDto.getTotal();
        return sum / (double) lastThreeMonthsSales.size();
    }
}
