package com.example.warehouseManagement.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Services.SalesOrderService;
import com.example.warehouseManagement.Services.StockService;

/**
 * Controller to handle dashboard requests.
 */
@Controller
@RequestMapping(value = "/")
public class DashBoardController {

    // private static final String DASH_BOARD_PATH = "dashboar";
    private final SalesOrderService salesOrderService;
    private final StockService stockService;

    /**
     * Constructor to inject the necessary dependencies.
     *
     * @param salesOrderService the SalesOrderService dependency
     * @param stockService      the StockService dependency
     */
    public DashBoardController(SalesOrderService salesOrderService, StockService stockService) {
        this.salesOrderService = salesOrderService;
        this.stockService = stockService;
    }

    /**
     * Handles GET requests for the dashboard.
     *
     * @param model the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @GetMapping(value = "/")
    public String getDashBoard(Model model) {
            model.addAttribute("title", "Dashboard");
            model.addAttribute("lastThreeMonthsSales", salesOrderService.findLastThreeMonthsSales());
            model.addAttribute("topFiveMovers", stockService.getTopFiveMovers());
            return "dashboard/dashboard"; // Returning the view template name
    }
}
