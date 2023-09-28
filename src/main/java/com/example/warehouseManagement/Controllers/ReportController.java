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

/**
 * Controller class for handling report generation requests.
 */
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

    /**
     * Constructor.
     *
     * @param salesOrderService the SalesOrderService to use
     * @param customerService   the CustomerService to use
     * @param stockService      the StockService to use
     * @param vendorService     the VendorService to use
     * @param backorderService  the BackorderService to use
     */
    public ReportController(SalesOrderService salesOrderService, CustomerService customerService,
            StockService stockService, VendorService vendorService, BackorderService backorderService) {
        this.salesOrderService = salesOrderService;
        this.customerService = customerService;
        this.stockService = stockService;
        this.vendorService = vendorService;
        this.backorderService = backorderService;
    }

    /**
     * Handles GET requests to fetch sales orders by customer.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = SALES_ORDER_BY_CUSTOMER_PATH)
    public String getSalesOrdersByCustomer(Model model) {
        // Add a new SalesOrderByCustomerDto object to the model.
        model.addAttribute("salesOrderByCustomerDto", new SalesOrderByCustomerDto());
        // Add a list of all customers to the model.
        model.addAttribute("customers", customerService.findAll());
        // Return the name of the view to render.
        return "reports/salesOrderByCustomer";
    }

    /**
     * Handles POST requests to print sales orders by customer.
     *
     * @param salesOrderByCustomerDto the SalesOrderByCustomerDto object containing
     *                                the selected customer ID
     * @param model                   the Model to populate with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = SALES_ORDER_BY_CUSTOMER_PATH)
    public String printSalesOrdersByCustomer(@ModelAttribute SalesOrderByCustomerDto salesOrderByCustomerDto,
            Model model) {
        // Get the selected customer ID.
        Long customerId = salesOrderByCustomerDto.getCustomerId();
        // Add a list of all sales orders for the selected customer to the model.
        model.addAttribute("salesOrders", salesOrderService.findAllByCustomer(customerId));
        // Add the selected customer to the model.
        model.addAttribute("customer", customerService.findById(customerId).get());
        return "reports/printSalesOrderByCustomer";
    }

    /**
     * Handles GET requests to fetch sales order details.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = SALES_ORDER_BY_DETAILS_PATH)
    public String getSalesOrderDetails(Model model) {
        // Add a new SalesOrderByNumberDto object to the model.
        model.addAttribute("salesOrderByNumberDto", new SalesOrderByNumberDto());
        // Add a list of all sales orders to the model.
        model.addAttribute("salesOrders", salesOrderService.findAll());
        // Return the name of the view to render.
        return "reports/salesOrderByNumber";
    }

    /**
     * Handles POST requests to print sales order details.
     *
     * @param salesOrderByNumberDto the SalesOrderByNumberDto object containing the
     *                              selected sales order number
     * @param model                 the Model to populate with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = SALES_ORDER_BY_DETAILS_PATH)
    public String printSalesOrderDetails(@ModelAttribute SalesOrderByNumberDto salesOrderByNumberDto,
            Model model) {
        // Get the selected sales order number.
        Long salesNumber = salesOrderByNumberDto.getId();
        // Add the selected sales order to the model.
        model.addAttribute("salesOrder", salesOrderService.findById(salesNumber).get());
        // Return the name of the view to render.
        return "reports/printSalesOrderDetails";
    }

    /**
     * Helper function that retuns a capitalized from of a given string
     * 
     * @param string
     * @return
     */
    public String capitalizeString(String string) {
        return string.substring(0, 1).toUpperCase()
                + string.substring(1).toLowerCase();
    }

    /**
     * Handles GET requests to fetch pending sales orders.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = PENDING_SALES_ORDERS_PATH)
    public String getPendingSalesOrders(Model model) {
        // Add a new PendingSalesOrderByYearAndMonthDto object to the model.
        model.addAttribute("pendingSalesOrderByYearAndMonthDto", new PendingSalesOrderByYearAndMonthDto());
        // Return the name of the view to render.
        return "reports/pendingSalesOrderByYearAndMonth";
    }

    /**
     * Handles POST requests to print pending sales orders by year and month.
     *
     * @param pendingSalesOrderByYearAndMonthDto the
     *                                           PendingSalesOrderByYearAndMonthDto
     *                                           object containing the selected year
     *                                           and month
     * @param model                              the Model to populate with data for
     *                                           the view
     * @return the name of the view to render
     */
    @PostMapping(value = PENDING_SALES_ORDERS_PATH)
    public String printSalesOrdersByYearAndMonth(
            @ModelAttribute PendingSalesOrderByYearAndMonthDto pendingSalesOrderByYearAndMonthDto,
            Model model) {
        // Get the selected year and month.
        int year = pendingSalesOrderByYearAndMonthDto.getYear();
        String month = pendingSalesOrderByYearAndMonthDto.getMonth();
        // Get the year of the month to be displayed on the report's header.
        LocalDate date = LocalDate.of(year, Integer.parseInt(month), 1);
        // Capitalize the month name.
        String capitalizedMonth = capitalizeString(date.getMonth().toString());
        // Add the capitalized month name and the selected year to the model.
        model.addAttribute("month", capitalizedMonth);
        model.addAttribute("year", date.getYear());
        // Add a list of all pending to ship sales orders for the selected year and
        // month to the model.
        model.addAttribute("salesOrders", salesOrderService.findAllPendingToShipOrdersByYearAndMonth(year, month));
        // Return the name of the view to render.
        return "reports/printPendingSalesOrderByYearAndMonth";
    }

    /**
     * Handles GET requests to fetch stock level report.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = STOCK_LEVEL_REPORT_BY_VENDOR_PATH)
    public String getStockLevelReport(Model model) {
        // Add a new StockLevelReportByVendorDto object to the model.
        model.addAttribute("stockLevelReportByVendor", new StockLevelReportByVendorDto());
        // Add a list of all vendors to the model.
        model.addAttribute("vendors", vendorService.findAll());
        // Return the name of the view to render.
        return "reports/stockLevelReportByVendor";
    }

    /**
     * Handles POST requests to print stock level report by vendor.
     *
     * @param stockLevelReportByVendor the StockLevelReportByVendorDto object
     *                                 containing the selected vendor ID
     * @param model                    the Model to populate with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = STOCK_LEVEL_REPORT_BY_VENDOR_PATH)
    public String printStockLevelReportByVendor(@ModelAttribute StockLevelReportByVendorDto stockLevelReportByVendor,
            Model model) {
        // Get the selected vendor ID.
        Long vendorId = stockLevelReportByVendor.getVendorId();
        // Add the current date and a list of all stock report items for the selected
        // vendor to the model.
        model.addAttribute("date", LocalDate.now());
        model.addAttribute("stockLevelReportItems", stockService.findStockReportsItemsByVendorId(vendorId));
        // Return the name of the view to render.
        return "reports/printStockLevelReportByVendor";
    }

    /**
     * Handles GET requests to fetch backorders report.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = BACKORDERS_REPORT_BY_YEAR_PATH)
    public String getBackordersReport(Model model) {
        // Add a new BackorderReportByYearDto object to the model.
        model.addAttribute("backorderReportByYearDto", new BackorderReportByYearDto());
        // Return the name of the view to render.
        return "reports/backorderReportByYear";
    }

    /**
     * Handles POST requests to print backorders report by year.
     *
     * @param backorderReportByYear the BackorderReportByYearDto object containing
     *                              the selected year
     * @param model                 the Model to populate with data for the view
     * @return the name of the view to render
     */

    @PostMapping(value = BACKORDERS_REPORT_BY_YEAR_PATH)
    public String printBackorderReport(@ModelAttribute BackorderReportByYearDto backorderReportByYear,
            Model model) {
        // Get the selected year.
        int year = backorderReportByYear.getYear();
        // Add the selected year and a list of all backorders for the selected year to the model.
        model.addAttribute("year", year);
        model.addAttribute("backorders", backorderService.findBackordersByYear(year));
        // Return the name of the view to render.
        return "reports/printBackorderReportByYear";
    }

}
