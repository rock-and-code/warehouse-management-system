package com.example.warehouseManagement.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.DTOs.SalesOrderByNumberDto;
import com.example.warehouseManagement.Services.SalesOrderService;

@Controller
@RequestMapping(value = "/reports")
public class SalesOrderDetailsController {

    private static final String SALES_ORDER_BY_DETAILS_PATH = "/sales-order-details";
    private final SalesOrderService salesOrderService;

    public SalesOrderDetailsController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @GetMapping(value = SALES_ORDER_BY_DETAILS_PATH)
    public String getSalesOrderDetails(Model model) {
        model.addAttribute("salesOrderByNumberDto", new SalesOrderByNumberDto());
        model.addAttribute("salesOrders", salesOrderService.findAll());
        return "reports/salesOrderByNumber";
    }

    @PostMapping(value = SALES_ORDER_BY_DETAILS_PATH)
    public String printSalesOrderDetails(@ModelAttribute SalesOrderByNumberDto salesOrderByNumberDto,
            Model model) {
        int salesNumber = salesOrderByNumberDto.getSalesOrderNumber();
        model.addAttribute("salesOrder", salesOrderService.findBySalesOrderNumber(salesNumber));
        return "reports/printSalesOrderDetails";
    }
}
