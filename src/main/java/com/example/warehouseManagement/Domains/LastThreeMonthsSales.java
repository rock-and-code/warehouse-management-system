package com.example.warehouseManagement.Domains;

import java.util.ArrayList;
import java.util.List;

import com.example.warehouseManagement.Domains.DTOs.MonthlySalesDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain class for representing the last three months sales.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LastThreeMonthsSales {
    /**
     * A list of monthly sales DTOs for the last three months.
     */
    @Builder.Default
    private List<MonthlySalesDto> lastThreeMonthsSales = new ArrayList<>();
    /**
     * Calculates the average sales for the last three months.
     *
     * @return the average sales for the last three months
     */
    public double getThreeMonthsAverageSales() {
        double sum = 0.0;
        for (MonthlySalesDto monthlySalesDto : lastThreeMonthsSales)
            sum += monthlySalesDto.getTotal();
        return sum / (double) lastThreeMonthsSales.size();
    }
    /**
     * Calculates the maximum sales for the last three months.
     *
     * @return the maximum sales for the last three months
     */
    public double getMaxValue() {
        double max = 0.0;
        for (MonthlySalesDto monthlySalesDto : lastThreeMonthsSales)
            max = Math.max(max, monthlySalesDto.getTotal());
        return max;
    }
}
