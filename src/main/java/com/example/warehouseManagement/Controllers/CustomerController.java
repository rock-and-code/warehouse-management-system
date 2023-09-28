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

/**
 * Controller class for handling customer operations.
 */
@Controller
@RequestMapping(value = "/")
public class CustomerController {

    private final CustomerService customerService;

    private static final String CUSTOMER_PATH = "customers";
    private static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    /**
     * Constructor.
     *
     * @param customerService the CustomerService to use
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Handles GET requests for retrieving a list of customers.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = CUSTOMER_PATH)
    public String getCustomers(Model model) {
        // Adding a list of customers and a title attribute to the model
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("title", "Customers");
        return "customers/customers"; // Returning the view template name
    }

    /**
     * Handles GET requests for retrieving customer details by ID.
     *
     * @param id the customer ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of customers if the customer is not found
     */
    @GetMapping(value = CUSTOMER_PATH_ID)
    public String getCustomerDetails(@PathVariable(name = "customerId", required = false) Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        // Checking if the customer exists
        if (!customer.isPresent())
            return "redirect:/customers"; // Redirecting to the list of customers

        // Adding the customer and a title attribute to the model
        model.addAttribute("customer", customer.get());
        model.addAttribute("title", "Customer Details");
        return "customers/details"; // Returning the view template name
    }

    /**
     * Handles POST requests for deleting a customer by ID.
     *
     * @param id the customer ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of customers if the customer is not found or cannot be deleted
     */
    @PostMapping(value = CUSTOMER_PATH_ID, params = "delete")
    public String deleteCustomer(@PathVariable(name = "customerId", required = false) Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
         // Check if the customer exists.
         if (!customer.isPresent()) {
            return "redirect:/customers?notFound"; // Redirect to the list of customers with an error message.
        }

        // Check if the customer has associated invoices.
        if (customer.get().getInvoices().size() > 0) {
            return "redirect:/customers?failedToDelete"; // Redirect to the list of customers with a failure message.
        }

        // Delete the customer and redirect to the list of customers with a success message.
        customerService.delete(customer.get());
        return "redirect:/customers/customerDeleted";
    }

}
