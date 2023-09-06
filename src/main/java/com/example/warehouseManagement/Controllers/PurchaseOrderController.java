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

    @GetMapping(value = NEW_PURCHASE_ORDER_PATH)
    public String getPurchaseOrderDetails(@ModelAttribute PurchaseOrder purchaseOrder, Model model) {
        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/purchaseOrderForm";
    }

    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "addRow")
    public String addPurchaseOrderLine(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        //Adding new sales order line to sales order
        purchaseOrder.getPurchaseOrderLines().add(new PurchaseOrderLine());

        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/purchaseOrderForm";
    }

    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "removeRow")
    public String removePurchaseOrderLine(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        // newSalesOrderDto.addOrderLine();
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        purchaseOrder.getPurchaseOrderLines().remove(rowId);

        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/purchaseOrderForm";
    }

    @PostMapping(value = NEW_PURCHASE_ORDER_PATH, params = "save")
    public String savePurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder,
            HttpServletRequest request, Model model) {
        PurchaseOrder savedPurchaseOrder = purchaseOrderService.save(purchaseOrder);
        System.out.printf("Sales Order: %d added to dba\n", savedPurchaseOrder.getPurchaseOrderNumber());
        return "redirect:/purchase-order";
    }


    @GetMapping(value = PURCHASE_ORDER_PATH)
    public String getAllPurchaseOrders(Model model) {
        //TODO: -> Add pagination (25 records per page)
        model.addAttribute("title", "Purchase Orders");
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("purchaseOrders", purchaseOrderService.findAllPurchaseOrder());
        return "purchaseOrders/purchaseOrders";
    }

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
            return "redirect:/purchase-order?orderNotFound";
        }   
    }

    @PostMapping(value = PURCHASE_ORDER_ID_PATH, params = "delete")
    public String deletePurchaseOrder(@PathVariable(value = "orderId") Long orderId, Model model) {
        Optional<PurchaseOrder> order = purchaseOrderService.findById(orderId);
        if (order.isPresent()) {
            System.out.println("Order " + order.get().getId() + " deleted");
            purchaseOrderService.delete(order.get());
            return "redirect:/purchase-order";
        }
        else {
            return "redirect:/purchase-order?orderNotFound";
        }   
    }

}

