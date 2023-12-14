package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.StatePool;
import com.example.warehouseManagement.Domains.Vendor;
import com.example.warehouseManagement.Services.VendorService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller class for handling customer operations.
 */
@Controller
@RequestMapping(value = "/")
public class VendorController {

    private final VendorService vendorService;

    private static final String VENDOR_PATH = "vendors";
    private static final String NEW_VENDOR_PATH = VENDOR_PATH + "/new-vendor";
    private static final String VENDOR_PATH_ID = VENDOR_PATH + "/{vendorId}";

    /**
     * Constructor.
     *
     * @param vendorService the CustomerService to use
     */
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Handles GET requests for retrieving a list of vendors.
     *
     * @param model the Model to populate with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = VENDOR_PATH)
    public String getVendors(Model model) {
        // Adding a list of vendors and a title attribute to the model
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("title", "Vendors");
        return "vendors/vendors"; // Returning the view template name
    }


    /**
     * GET /vendors/new-vendor
     *
     * Displays a form for creating a new vendor.
     *
     * @param vendor a Customer object to be populated with form data
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = NEW_VENDOR_PATH)
    public String newCustomer(@ModelAttribute Vendor mewVendor, Model model) {
        model.addAttribute("states", StatePool.getStates());
        model.addAttribute("newVendor", Vendor.builder().build());
        return "vendors/newVendorForm";
    }

      /**
     * POST /vendors/new-vendor?save
     *
     * Saves the vendor.
     *
     * @param vendor a Vendor object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the vendors
     *         list page if the new vendor is saved successfully
     */
    @PostMapping(value = NEW_VENDOR_PATH)
    public String saveVendor(@ModelAttribute Vendor newVendor,
            HttpServletRequest request, Model model) {
        vendorService.save(newVendor);
        // Redirect to the vendors list page if the new vendor is saved
        // successfully.
        return "redirect:/vendors?added";
    }

    /**
     * Handles GET requests for retrieving vendor details by ID.
     *
     * @param id the vendor ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of vendor if the vendor is not found
     */
    @GetMapping(value = VENDOR_PATH_ID)
    public String getVendorDetails(@PathVariable(name = "vendorId", required = false) Long id, Model model) {
        Optional<Vendor> vendor = vendorService.findById(id);
        // Checking if the customer exists
        if (vendor.isEmpty())
            return "redirect:/vendors"; // Redirecting to the list of vendors

        // Adding the vendor and a title attribute to the model
        model.addAttribute("vendor", vendor.get());
        model.addAttribute("title", "Vendor Details");
        return "vendors/vendorDetails"; // Returning the view template name
    }

    /**
     * Handles POST requests for deleting a vendor by ID.
     *
     * @param id the vemdpor ID
     * @param model the Model to populate with data for the view
     * @return the name of the view to render, or a redirect to the list of vendor if the vendor is not found or cannot be deleted
     */
    @PostMapping(value = VENDOR_PATH_ID, params = "delete")
    public String deleteVendor(@PathVariable(name = "vendorId", required = false) Long id, Model model) {
        Optional<Vendor> vendor = vendorService.findById(id);
         // Check if the vendor exists.
         if (vendor.isEmpty()) {
            return "redirect:/vendors?notFound"; // Redirect to the list of vendors with an error message.
        }

        // Check if the vendor has associated purchase orders.
        if (vendor.get().getPurchaseOrders().size() > 0) {
            return "redirect:/vendors?failedToDelete"; // Redirect to the list of vendors with a failure message.
        }

        // Delete the customer and redirect to the list of customers with a success message.
        vendorService.delete(vendor.get());
        return "redirect:/vendor/deleted";
    }

}

