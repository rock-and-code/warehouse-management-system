package com.example.warehouseManagement.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.DTOs.SalesOrderByCustomerDto;
import com.example.warehouseManagement.Services.CustomerService;
import com.example.warehouseManagement.Services.SalesOrderService;

@Controller
@RequestMapping(value = "/reports")
public class SalesOrderByCustomerController {

    private static final String SALES_ORDER_BY_CUSTOMER_PATH = "/sales-order-by-customer";
    private final SalesOrderService salesOrderService;
    private final CustomerService customerService;

    public SalesOrderByCustomerController(SalesOrderService salesOrderService, CustomerService customerService) {
        this.salesOrderService = salesOrderService;
        this.customerService = customerService;
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
    
}
