package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.StatePool;
import com.example.warehouseManagement.Domains.DTOs.AdvancedCustomerSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.TextMode;
import com.example.warehouseManagement.Domains.Exceptions.CustomerNotFoundException;
import com.example.warehouseManagement.Services.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller class for handling customer operations.
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    private static final String NEW_CUSTOMER_PATH = "/new-customer";
    private static final String CUSTOMER_ID_PATH = "/{customerId}";
    private static final String UPDATE_CUSTOMER_ID_PATH = CUSTOMER_ID_PATH + "/update";

    private final CustomerService customerService;

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
    @GetMapping
    public String getCustomers(
            @ModelAttribute("criteria") AdvancedCustomerSearchCriteria criteria,
            @PageableDefault(size = 25, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {
        model.addAttribute("page", customerService.findAdvanced(criteria, pageable));
        model.addAttribute("title", "Customers");
        model.addAttribute("textModes", TextMode.values());
        return "customers/customers";
    }


    /**
     * GET /customers/new-customer
     *
     * Displays a form for creating a new customer.
     *
     * @param customer a Customer object to be populated with form data
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(NEW_CUSTOMER_PATH)
    public String newCustomer(@ModelAttribute Customer newCustomer, Model model) {
        model.addAttribute("states", StatePool.getStates());
        model.addAttribute("newCustomer", Customer.builder().build());
        return "customers/newCustomerForm";
    }

      /**
     * POST /customers/new-customer?save
     *
     * Saves the customer.
     *
     * @param customer a Customer object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the customers
     *         list page if the customer is saved successfully
     */
    @PostMapping(NEW_CUSTOMER_PATH)
    public String saveCustomer(@ModelAttribute Customer newCustomer,
            HttpServletRequest request, Model model) {
        customerService.save(newCustomer);
        // Redirect to the customer list page if the customer is saved
        // successfully.
        return "redirect:/customers?added";
    }

    /**
     * Handles GET requests for retrieving customer details by ID.
     *
     * @param id the customer ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of customers if the customer is not found
     */
    @GetMapping(CUSTOMER_ID_PATH)
    public String getCustomerDetails(@PathVariable(name = "customerId", required = false) Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        // Checking if the customer exists
        if (customer.isEmpty())
            return "redirect:/customers"; // Redirecting to the list of customers

        // Adding the customer and a title attribute to the model
        model.addAttribute("customer", customer.get());
        model.addAttribute("title", "Customer Details");
        return "customers/customerDetails"; // Returning the view template name
    }

     /**
     * Handles GET requests for retrieving customer details by ID and display a form to update customer details.
     *
     * @param id the customer ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of customers if the customer is not found
     */
    @GetMapping(UPDATE_CUSTOMER_ID_PATH)
    public String getCustomerUpdateForm(@PathVariable(name = "customerId", required = false) Long id, Model model) {
           Optional<Customer> customer = customerService.findById(id);
        // Checking if the customer exists
        if (customer.isEmpty())
            return "redirect:/customers"; // Redirecting to the list of customers

        // Adding the customer and a title attribute to the model
        model.addAttribute("customer", customer.get());
        model.addAttribute("states", StatePool.getStates());
        model.addAttribute("title", "Update Customer");
        return "customers/updateCustomerForm"; // Returning the view template name
    }

    /**
     * Handles POST requests to update data from an existing customer in database
     *
     * @param id the customer ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of customers if the customer is not found
     */
    @PostMapping(UPDATE_CUSTOMER_ID_PATH)
    public String updateCustomer(@PathVariable(name = "customerId", required = false) Long id,
        @ModelAttribute Customer updatedCustomer, HttpServletRequest request, Model model)
            throws CustomerNotFoundException {
        customerService.updateById(id, updatedCustomer);
        return String.format("redirect:/customers/%d", id);
    }

    /**
     * Handles POST requests for deleting a customer by ID.
     *
     * @param id the customer ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of customers if the customer is not found or cannot be deleted
     */
    @PostMapping(value = CUSTOMER_ID_PATH, params = "delete")
    public String deleteCustomer(@PathVariable(name = "customerId", required = false) Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
         // Check if the customer exists.
         if (customer.isEmpty()) {
            return "redirect:/customers?notFound"; // Redirect to the list of customers with an error message.
        }

        // Check if the customer has associated invoices.
        if (customer.get().getInvoices().size() > 0) {
            return "redirect:/customers?failedToDelete"; // Redirect to the list of customers with a failure message.
        }

        // Delete the customer and redirect to the list of customers with a success message.
        customerService.delete(customer.get());
        return "redirect:/customers?deleted";
    }

}
