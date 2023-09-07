package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrder.Status;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Services.CustomerService;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.SalesOrderService;
import com.example.warehouseManagement.Util.Counter;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/")
public class SalesOrderController {

    private static final String SALES_ORDER_PATH = "sales-order";
    private static final String NEW_SALES_ORDER_PATH = SALES_ORDER_PATH + "/new-sales-order";
    private static final String SALES_ORDER_ID_PATH = SALES_ORDER_PATH + "/{orderId}";
    private final SalesOrderService salesOrderService;
    private final CustomerService customerService;
    private final ItemService itemService;

    public SalesOrderController(SalesOrderService salesOrderService, CustomerService customerService,
            ItemService itemService) {
        this.salesOrderService = salesOrderService;
        this.customerService = customerService;
        this.itemService = itemService;
    }

    @GetMapping(value = NEW_SALES_ORDER_PATH)
    public String getSalesOrderDetails(@ModelAttribute SalesOrder salesOrder, Model model) {
        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/salesOrderForm";
    }

    @PostMapping(value = NEW_SALES_ORDER_PATH, params = "addRow")
    public String addSaleOrderLine(@ModelAttribute SalesOrder salesOrder,
            HttpServletRequest request, Model model) {
        //Adding new sales order line to sales order
        salesOrder.getSaleOrderLines().add(new SalesOrderLine());

        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/salesOrderForm";
    }

    @PostMapping(value = NEW_SALES_ORDER_PATH, params = "removeRow")
    public String removeSaleOrderLine(@ModelAttribute SalesOrder salesOrder,
            HttpServletRequest request, Model model) {
        // newSalesOrderDto.addOrderLine();
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        salesOrder.getSaleOrderLines().remove(rowId);

        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "forms/salesOrderForm";
    }

    @PostMapping(value = NEW_SALES_ORDER_PATH, params = "save")
    public String saveSaleOrder(@ModelAttribute SalesOrder salesOrder,
            HttpServletRequest request, Model model) {
        SalesOrder savedSalesOrder = salesOrderService.save(salesOrder);
        System.out.printf("Sales Order: %d added to dba\n", savedSalesOrder.getSalesOrderNumber());
        return "redirect:/sales-order?added";
    }


    @GetMapping(value = SALES_ORDER_PATH)
    public String getAllSalesOrders(Model model) {
        //TODO: -> Add pagination (25 records per page)
        model.addAttribute("title", "Sales Orders");
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("salesOrders", salesOrderService.findAllSalesOrder());
        return "salesOrders/salesOrders";
    }

    @GetMapping(value = SALES_ORDER_ID_PATH)
    public String getSalesOrderDetails(@PathVariable(value = "orderId") Long orderId, Model model) {
        Optional<SalesOrder> order = salesOrderService.findById(orderId);
        if (order.isPresent()) {
            model.addAttribute("title", "Sales Orders");
            model.addAttribute("salesOrder", order.get());
            model.addAttribute("counter", new Counter());
        return "salesOrders/salesOrderDetails";
        }
        else {
            return "redirect:/sales-order?notFound";
        }   
    }

    @PostMapping(value = SALES_ORDER_ID_PATH, params = "delete")
    public String deleteSalesOrder(@PathVariable(value = "orderId") Long orderId, Model model) {
        Optional<SalesOrder> order = salesOrderService.findById(orderId);
        if (order.isPresent()) {
            if (order.get().getStatus() != Status.SHIPPED) {
                salesOrderService.delete(order.get());
                return "redirect:/sales-order";
            }
            else {
                return "redirect:/sales-order/?failedToDelete";
            }
        }
        else {
            return "redirect:/sales-order/?notFound";
        }   
    }

}
