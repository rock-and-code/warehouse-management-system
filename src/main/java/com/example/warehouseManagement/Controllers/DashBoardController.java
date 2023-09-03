package com.example.warehouseManagement.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Services.SalesOrderService;
import com.example.warehouseManagement.Services.StockService;

@Controller
@RequestMapping(value = "/")
public class DashBoardController {

    // private static final String DASH_BOARD_PATH = "dashboar";
    private final SalesOrderService salesOrderService;
    private final StockService stockService;

    public DashBoardController(SalesOrderService salesOrderService, StockService stockService) {
        this.salesOrderService = salesOrderService;
        this.stockService = stockService;
    }

    @GetMapping(value = "/")
    public String getDashBoard(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("lastThreeMonthsSales", salesOrderService.findLastThreeMonthsSales());
        model.addAttribute("topFiveMovers", stockService.getTopFiveMovers());
        return "dashboard/dashboard";
    }
}
