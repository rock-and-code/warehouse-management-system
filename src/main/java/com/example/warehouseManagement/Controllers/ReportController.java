package com.example.warehouseManagement.Controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.DTOs.BackorderReportByYearDto;
import com.example.warehouseManagement.Domains.DTOs.PendingSalesOrderByYearAndMonthDto;
import com.example.warehouseManagement.Domains.DTOs.SalesOrderByCustomerDto;
import com.example.warehouseManagement.Domains.DTOs.SalesOrderByNumberDto;
import com.example.warehouseManagement.Domains.DTOs.StockLevelReportByVendorDto;
import com.example.warehouseManagement.Services.BackorderService;
import com.example.warehouseManagement.Services.CustomerService;
import com.example.warehouseManagement.Services.SalesOrderService;
import com.example.warehouseManagement.Services.StockService;
import com.example.warehouseManagement.Services.VendorService;


@Controller
@RequestMapping(value = "/reports")
public class ReportController {

    private static final String SALES_ORDER_BY_CUSTOMER_PATH = "/sales-order-by-customer";
    private static final String SALES_ORDER_BY_DETAILS_PATH = "/sales-order-details";
    private static final String PENDING_SALES_ORDERS_PATH = "/pending-sales-orders-by-year-month";
    private static final String STOCK_LEVEL_REPORT_BY_VENDOR_PATH = "/stock-level-report-by-vendor";
    private static final String BACKORDERS_REPORT_BY_YEAR_PATH = "/back-order-report-by-year";
    private final SalesOrderService salesOrderService;
    private final CustomerService customerService;
    private final StockService stockService;
    private final VendorService vendorService;
    private final BackorderService backorderService;

    public ReportController(SalesOrderService salesOrderService, CustomerService customerService,
            StockService stockService, VendorService vendorService, BackorderService backorderService) {
        this.salesOrderService = salesOrderService;
        this.customerService = customerService;
        this.stockService = stockService;
        this.vendorService = vendorService;
        this.backorderService = backorderService;
    }

    @GetMapping(value = SALES_ORDER_BY_CUSTOMER_PATH)
    public String getSalesOrdersByCustomer(Model model) {
        model.addAttribute("salesOrderByCustomerDto", new SalesOrderByCustomerDto());
        model.addAttribute("customers", customerService.findAll());
        return "reports/salesOrderByCustomer";
    }

    @PostMapping(value = SALES_ORDER_BY_CUSTOMER_PATH)
    public String printSalesOrdersByCustomer(@ModelAttribute SalesOrderByCustomerDto salesOrderByCustomerDto ,
            Model model) {
        Long customerId = salesOrderByCustomerDto.getCustomerId();
        model.addAttribute("salesOrders", salesOrderService.findAllByCustomer(customerId));
        model.addAttribute("customer", customerService.findById(customerId).get());
        return "reports/printSalesOrderByCustomer";
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
        Long salesNumber = salesOrderByNumberDto.getId();
        model.addAttribute("salesOrder", salesOrderService.findById(salesNumber).get());
        return "reports/printSalesOrderDetails";
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

    @GetMapping(value = STOCK_LEVEL_REPORT_BY_VENDOR_PATH)
    public String getStockLevelReport(Model model) {
        model.addAttribute("stockLevelReportByVendor", new StockLevelReportByVendorDto());
        model.addAttribute("vendors", vendorService.findAll());
        return "reports/stockLevelReportByVendor";
    }

    @PostMapping(value = STOCK_LEVEL_REPORT_BY_VENDOR_PATH)
    public String printStockLevelReportByVendor(@ModelAttribute StockLevelReportByVendorDto stockLevelReportByVendor,
            Model model) {
        Long vendorId = stockLevelReportByVendor.getVendorId();
        model.addAttribute("date", LocalDate.now());
        model.addAttribute("stockLevelReportItems", stockService.findStockReportsItemsByVendorId(vendorId));
        return "reports/printStockLevelReportByVendor";
    }

    @GetMapping(value = BACKORDERS_REPORT_BY_YEAR_PATH)
    public String getBackordersReport(Model model) {
        model.addAttribute("backorderReportByYearDto", new BackorderReportByYearDto());
        return "reports/backorderReportByYear";
    }

    @PostMapping(value = BACKORDERS_REPORT_BY_YEAR_PATH)
    public String printBackorderReport(@ModelAttribute BackorderReportByYearDto backorderReportByYear,
            Model model) {
        int year = backorderReportByYear.getYear();
        model.addAttribute("year", year);
        model.addAttribute("backorders", backorderService.findBackordersByYear(year));
        return "reports/printBackorderReportByYear";
    }

    
    
}

    
