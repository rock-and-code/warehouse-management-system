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
import com.example.warehouseManagement.Domains.Exceptions.PurchaseOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ReceivedOrderModificationException;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.PurchaseOrderLineService;
import com.example.warehouseManagement.Services.PurchaseOrderService;
import com.example.warehouseManagement.Services.VendorService;
import com.example.warehouseManagement.Util.Counter;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/")
public class PurchaseOrderController {

    private static final String PURCHASE_ORDER_PATH = "purchase-orders";
    private static final String PENDING_PURCHASE_ORDER_PATH = PURCHASE_ORDER_PATH + "/pending";
    private static final String NEW_PURCHASE_ORDER_PATH = PURCHASE_ORDER_PATH + "/new-purchase-order";
    private static final String PURCHASE_ORDER_ID_PATH = PURCHASE_ORDER_PATH + "/{orderId}";
    private static final String UPDATE_PURCHASE_ORDER_ID_PATH = PURCHASE_ORDER_ID_PATH + "/update";
    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderLineService purchaseOrderLineService;
    private final VendorService vendorService;
    private final ItemService itemService;

    /**
     * Constructor
     * @param purchaseOrderService the purchaseOrderService to use
     * @param purchaseOrderLineService the purchaseOrderLineService to use
     * @param vendorService the vendorService to use
     * @param itemService the itemService to use
     */
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, PurchaseOrderLineService purchaseOrderLineService,
            VendorService vendorService, ItemService itemService) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseOrderLineService = purchaseOrderLineService;
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
        return "purchaseOrders/newPurchaseOrderForm";
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
        return "purchaseOrders/newPurchaseOrderForm";
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
        // newpurchaseOrderDto.addOrderLine();
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        // Remove the purchase order line based on the row ID
        purchaseOrder.getPurchaseOrderLines().remove(rowId);

        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "purchaseOrders/newPurchaseOrderForm";
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
        return "redirect:/purchase-orders?added";
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
     * GET /sales-order/pending
     *
     * Gets all sales orders.
     *
     * @param model a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = PENDING_PURCHASE_ORDER_PATH)
    public String getAllPendingPurchaseOrders(Model model) {
        // TODO: -> Add pagination (25 records per page)
        // Add the title, customers, and sales orders to the model.
        model.addAttribute("title", "Purchase Orders");
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("purchaseOrders", purchaseOrderService.findAllPendingPurchaseOrder());
        // Return the name of the view to render.
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
     * GET /purchase-order/{orderId}/update
     *
     * Gets the purchase order update form for the given purchase order ID.
     *
     * @param orderId the purchase order ID
     * @param model   a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the purchase orders
     *         list page if the purchase order is not found
     */
    @GetMapping(value = UPDATE_PURCHASE_ORDER_ID_PATH)
    public String getUpdatePurchaseOrderForm(@PathVariable Long orderId, Model model) {
        Optional<PurchaseOrder> order = purchaseOrderService.findById(orderId);
        // If the purchase order is found, add it to the model and return the name of the
        // view to render.
        if (order.isPresent()) {
            model.addAttribute("title", "Purchase Orders");
            model.addAttribute("vendors", vendorService.findAll());
            model.addAttribute("purchaseOrder", order.get());
            model.addAttribute("persistedOrder", order.get());
            model.addAttribute("counter", new Counter());
            return "purchaseOrders/updatePurchaseOrderForm";
        } else {
            // The purchase order is not found, redirect to the purchase orders list page.
            return "redirect:/purchase-orders?notFound";
        }
    }

    /**
     * POST /purchase-order/{purchaseOrderId}/update?addRow
     *
     * Adds a new purchase order line to the update purchase order form.
     *
     * @param purchaseOrder a purchaseOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = UPDATE_PURCHASE_ORDER_ID_PATH, params = "addRow")
    public String addPurchaseOrderLineToUpdateOrderForm(@ModelAttribute PurchaseOrder purchaseOrder,
            @PathVariable("orderId") Long id, HttpServletRequest request, Model model) {
        // Adding new purchase order line to purchase order
        PurchaseOrder order = purchaseOrderService.findById(id).get();
        purchaseOrder.getPurchaseOrderLines().add(new PurchaseOrderLine());

        model.addAttribute("persistedOrder", order);
        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "purchaseOrders/updatePurchaseOrderForm";
    }

    /**
     * POST /purchase-order/{purchaseOrderId}/update?removeRow
     *
     * Removes a purchase order line from the update purchase order form.
     *
     * @param purchaseOrder a purchaseOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = UPDATE_PURCHASE_ORDER_ID_PATH, params = "removeRow")
    public String removePurchaseOrderLineFromUpdateOrderForm(@ModelAttribute PurchaseOrder purchaseOrder,
            @PathVariable("orderId") Long id, HttpServletRequest request, Model model) {
        PurchaseOrder order = purchaseOrderService.findById(id).get();
        // Checks if other was already received
        if (purchaseOrder.getStatus() == PoStatus.RECEIVED) {
            return "purchaseOrders/cannotBeUpdated";
        }
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        // Delete Line
        PurchaseOrderLine deletepurchaseOrderLine = order.getPurchaseOrderLines().get(rowId);
        purchaseOrder.getPurchaseOrderLines().remove(rowId);
        order.getPurchaseOrderLines().remove(rowId);
        purchaseOrderLineService.delete(deletepurchaseOrderLine);

        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("persistedOrder", order);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "purchaseOrders/updatepurchaseOrderForm";
    }

    /**
     * POST /purchase-order/{purchaseOrderId}/update?save
     *
     * Update an existing purchase order by a given id.
     *
     * @param purchaseOrder a purchaseOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the purchase orders
     *         list page if the purchase order is saved successfully
     */
    @PostMapping(value = UPDATE_PURCHASE_ORDER_ID_PATH, params = "save")
    public String updatepurchaseOrder(@PathVariable("orderId") Long id, @ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        try {
            purchaseOrderService.updateById(id, purchaseOrder);
        } catch (PurchaseOrderNotFoundException e) {
            return "redirect:/purchase-orders?notFound";
        } catch (ReceivedOrderModificationException e) {
            return "redirect:/purchase-orders?cannotBeUpdated";
        }
        // Redirect to the purchase orders list page if the purchase order was updated
        // successfully.
        return "redirect:/purchase-orders?updated";
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
                return "redirect:/purchase-orders?purchaseOrderDeleted";
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
