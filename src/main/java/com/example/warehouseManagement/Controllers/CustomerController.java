package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Services.CustomerService;

@Controller
@RequestMapping(value = "/")
public class CustomerController {

    private final CustomerService customerService;

    private static final String CUSTOMER_PATH = "customers";
    private static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(value = CUSTOMER_PATH)
    public String getCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("title", "Customers");
        return "customers/customers";
    }

    @GetMapping(value = CUSTOMER_PATH_ID)
    public String getCustomerDetails(@PathVariable(name = "customerId", required = false) Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);

        if (!customer.isPresent())
            return "redirect:/customers";

        model.addAttribute("customer", customer.get());
        model.addAttribute("title", "Customer Details");
        return "customers/details";
    }

    @PostMapping(value = CUSTOMER_PATH_ID)
    public String deleteCustomer(@PathVariable(name = "customerId", required = false) Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);

        if (!customer.isPresent())
            return "redirect:/customers";
        customerService.delete(customer.get());
        return "redirect:/customers?customerDeleted";
    }
    
}
