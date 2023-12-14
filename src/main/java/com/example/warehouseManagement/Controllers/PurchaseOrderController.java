package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrder.PoStatus;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.VendorService;
import com.example.warehouseManagement.Util.Counter;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/")
public class PurchaseOrderController {

    private static final String PURCHASE_ORDER_PATH = "purchase-orders";
    private static final String NEW_PURCHASE_ORDER_PATH = PURCHASE_ORDER_PATH + "/new-purchase-order";
    private static final String PURCHASE_ORDER_ID_PATH = PURCHASE_ORDER_PATH + "/{orderId}";
    private final PurchaseOrderService purchaseOrderService;
    private final VendorService vendorService;
    private final ItemService itemService;

    /**
     * Constructor
     * @param purchaseOrderService the purchaseOrderService to use
     * @param vendorService the vendorService to use
     * @param itemService the itemService to use
     */
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, VendorService vendorService,
            ItemService itemService) {
        this.purchaseOrderService = purchaseOrderService;
        this.vendorService = vendorService;
        this.itemService = itemService;
    }

    // TODO: Add controller to display pending purchase orders only

    /**
     * Handles a GET request to create a new purchase order.
     *
     * @param model the Model object to populate with data for the view
     * @return the name of the view template to render
     */

    @GetMapping(value = NEW_PURCHASE_ORDER_PATH)
    public String newPurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder, Model model) {
        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "purchaseOrders/purchaseOrderForm";
    }

    /**
     * Handles a POST request to add a new line to the purchase order.
     *
     * @param purchaseOrder the purchase order
     * @param request       the HttpServletRequest object
     * @param model         the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "addRow")
    public String addPurchaseOrderLine(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        // Adding a new purchase order line to the current purchase order
        purchaseOrder.getPurchaseOrderLines().add(new PurchaseOrderLine());

        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "purchaseOrders/purchaseOrderForm";
    }

    /**
     * Handles a POST request to remove a line from the purchase order.
     *
     * @param purchaseOrder the purchase order
     * @param request       the HttpServletRequest object
     * @param model         the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "removeRow")
    public String removePurchaseOrderLine(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        // newSalesOrderDto.addOrderLine();
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        // Remove the purchase order line based on the row ID
        purchaseOrder.getPurchaseOrderLines().remove(rowId);

        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "purchaseOrders/purchaseOrderForm";
    }

    /**
     * Handles a POST request to save the purchase order.
     *
     * @param purchaseOrder the purchase order to save
     * @param request       the HttpServletRequest object
     * @param model         the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "save")
    public String savePurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        // Save the purchase order to the database
        purchaseOrderService.save(purchaseOrder);
        // Redirects to the purchase order list page.
        return "redirect:/purchase-orders";
    }

    /**
     * Handles a GET request to fetch all purchase orders.
     *
     * @param model the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @GetMapping(value = PURCHASE_ORDER_PATH)
    public String getAllPurchaseOrders(Model model) {
        // TODO: -> Add pagination (25 records per page)
        // Set the tittle, add the list of vendors and a list of purchase orders to the
        // model
        model.addAttribute("title", "Purchase Orders");
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("purchaseOrders", purchaseOrderService.findAllPurchaseOrder());
        // Returns the name of the view template to render.
        return "purchaseOrders/purchaseOrders";
    }

    /**
     * Handles a GET request to fetch details of a specific purchase order.
     *
     * @param orderId the ID of the purchase order to fetch details of
     * @param model   the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @GetMapping(value = PURCHASE_ORDER_ID_PATH)
    public String getPurchaseOrderDetails(@PathVariable Long orderId, Model model) {
        // Finds the purchase order with the specified ID.
        Optional<PurchaseOrder> order = purchaseOrderService.findById(orderId);
        // If the purchase order is found, sets the title, purchase order, and counter
        // to
        // the model and returns the name of the view template to render.
        if (order.isPresent()) {
            model.addAttribute("title", "Purchase Order Details");
            model.addAttribute("purchaseOrder", order.get());
            model.addAttribute("counter", new Counter());
            return "purchaseOrders/purchaseOrderDetails";
        } else {
            // If the purchase order is not found, redirects to the purchase order list page
            // with a not found error message.
            return "redirect:/purchase-orders?notFound";
        }
    }

    /**
     * Handles a POST request to delete a purchase order.
     *
     * @param orderId the ID of the purchase order to delete
     * @param model   the Model object to populate with data for the view
     * @return the name of the view template to render
     */
    @PostMapping(value = PURCHASE_ORDER_ID_PATH, params = "delete")
    public String deletePurchaseOrder(@PathVariable Long orderId, Model model) {
        // Finds the purchase order with the specified ID.
        Optional<PurchaseOrder> order = purchaseOrderService.findById(orderId);
        // If the purchase order is found, deletes it if its status is not received.
        if (order.isPresent()) {
            if (order.get().getStatus() != PoStatus.RECEIVED) {
                // Deletes the purchase order from the database.
                purchaseOrderService.delete(order.get());
                // Redirects to the purchase order list page.
                return "redirect:/purchase-orders";
            } else {
                // Redirects to the purchase order list page with a failed to delete error
                // message.
                return "redirect:/purchase-orders?failedToDelete";
            }
        } else {
            // Redirects to the purchase order list page with a not found error message.
            return "redirect:/purchase-orders?notFound";
        }
    }

}
