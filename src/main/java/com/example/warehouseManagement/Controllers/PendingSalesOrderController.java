package com.example.warehouseManagement.Controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.DTOs.PendingSalesOrderByYearAndMonthDto;
import com.example.warehouseManagement.Services.SalesOrderService;

@Controller
@RequestMapping(value = "/reports")
public class PendingSalesOrderController {

    private static final String PENDING_SALES_ORDERS_PATH = "/pending-sales-orders-by-year-month";
    private final SalesOrderService salesOrderService;

    public PendingSalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    /**
     * Helper function that retuns a capitalized from of a given string
     * @param string
     * @return
     */
    public String capitalizeString(String string) {
        return string.substring(0, 1).toUpperCase() 
            + string.substring(1).toLowerCase();
    }

    @GetMapping(value = PENDING_SALES_ORDERS_PATH)
    public String getPendingSalesOrders(Model model) {
        model.addAttribute("pendingSalesOrderByYearAndMonthDto", new PendingSalesOrderByYearAndMonthDto());
        return "reports/pendingSalesOrderByYearAndMonth";
    }

    @PostMapping(value = PENDING_SALES_ORDERS_PATH)
    public String printSalesOrdersByYearAndMonth(
            @ModelAttribute PendingSalesOrderByYearAndMonthDto pendingSalesOrderByYearAndMonthDto,
            Model model) {
        int year = pendingSalesOrderByYearAndMonthDto.getYear();
        String month = pendingSalesOrderByYearAndMonthDto.getMonth();
        //To get the year of a month to be display on the report's header
        LocalDate date = LocalDate.of(year, Integer.parseInt(month), 1);
        model.addAttribute("month", capitalizeString(date.getMonth().toString()));
        model.addAttribute("year", date.getYear());
        model.addAttribute("salesOrders", salesOrderService.findAllPendingToShipOrdersByYearAndMonth(year, month));
        return "reports/printPendingSalesOrderByYearAndMonth";
    }
}
