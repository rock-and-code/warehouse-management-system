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

    private static final String PURCHASE_ORDER_PATH = "purchase-order";
    private static final String NEW_PURCHASE_ORDER_PATH = PURCHASE_ORDER_PATH + "/new-purchase-order";
    private static final String PURCHASE_ORDER_ID_PATH = PURCHASE_ORDER_PATH + "/{orderId}";
    private final PurchaseOrderService purchaseOrderService;
    private final VendorService vendorService;
    private final ItemService itemService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, VendorService vendorService,
            ItemService itemService) {
        this.purchaseOrderService = purchaseOrderService;
        this.vendorService = vendorService;
        this.itemService = itemService;
    }

    // Handle GET request to create a new purchase order
    @GetMapping(value = NEW_PURCHASE_ORDER_PATH)
    public String newPurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder, Model model) {
        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/purchaseOrderForm";
    }

    // Handle POST request to add a new line to the purchase order
    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "addRow")
    public String addPurchaseOrderLine(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        // Adding a new purchase order line to the current purchase order
        purchaseOrder.getPurchaseOrderLines().add(new PurchaseOrderLine());

        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/purchaseOrderForm";
    }

    // Handle POST request to remove a line from the purchase order
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
        return "forms/purchaseOrderForm";
    }

    // Handle POST request to save the purchase order
    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "save")
    public String savePurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        // Save the purchase order
        purchaseOrderService.save(purchaseOrder);
        return "redirect:/purchase-order";
    }

    // Handle GET request to fetch all purchase orders
    @GetMapping(value = PURCHASE_ORDER_PATH)
    public String getAllPurchaseOrders(Model model) {
        //TODO: -> Add pagination (25 records per page)
        model.addAttribute("title", "Purchase Orders");
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("purchaseOrders", purchaseOrderService.findAllPurchaseOrder());
        return "purchaseOrders/purchaseOrders";
    }

    // Handle GET request to fetch details of a specific purchase order
    @GetMapping(value = PURCHASE_ORDER_ID_PATH)
    public String getPurchaseOrderDetails(@PathVariable(value = "orderId") Long orderId, Model model) {
        Optional<PurchaseOrder> order = purchaseOrderService.findById(orderId);
        if (order.isPresent()) {
            model.addAttribute("title", "Purchase Order Details");
            model.addAttribute("purchaseOrder", order.get());
            model.addAttribute("counter", new Counter());
        return "purchaseOrders/purchaseOrderDetails";
        }
        else {
            return "redirect:/purchase-order?notFound";
        }   
    }

    // Handle POST request to delete a purchase order
    @PostMapping(value = PURCHASE_ORDER_ID_PATH, params = "delete")
    public String deletePurchaseOrder(@PathVariable(value = "orderId") Long orderId, Model model) {
        Optional<PurchaseOrder> order = purchaseOrderService.findById(orderId);
        if (order.isPresent()) {
            if (order.get().getStatus() != PoStatus.RECEIVED) {
                // Delete the purchase order if its status is not received
                purchaseOrderService.delete(order.get());
                return "redirect:/purchase-order";
            }
            else {
                return "redirect:/purchase-order?failedToDelete";
            }
        }
        else {
            return "redirect:/purchase-order?notFound";
        }   
    }

}

