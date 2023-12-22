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
import com.example.warehouseManagement.Domains.SalesOrder.SoStatus;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Domains.Exceptions.SalesOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ShippedOrderModificationException;
import com.example.warehouseManagement.Services.CustomerService;
import com.example.warehouseManagement.Services.ItemService;
import com.example.warehouseManagement.Services.SaleOrderLineService;
import com.example.warehouseManagement.Services.SalesOrderService;
import com.example.warehouseManagement.Util.Counter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller class for handling sales order operations.
 */
@Controller
@RequestMapping(value = "/")
public class SalesOrderController {

    private static final String SALES_ORDER_PATH = "sales-orders";
    private static final String PENDING_SALES_ORDER_PATH = SALES_ORDER_PATH + "/pending";
    private static final String NEW_SALES_ORDER_PATH = SALES_ORDER_PATH + "/new-sales-order";
    private static final String SALES_ORDER_ID_PATH = SALES_ORDER_PATH + "/{orderId}";
    private static final String UPDATE_SALES_ORDER_ID_PATH = SALES_ORDER_ID_PATH + "/update";

    private final SalesOrderService salesOrderService;
    private final SaleOrderLineService salesOrderLineService;
    private final CustomerService customerService;
    private final ItemService itemService;

    public SalesOrderController(SalesOrderService salesOrderService, SaleOrderLineService salesOrderLineService,
            CustomerService customerService, ItemService itemService) {
        this.salesOrderService = salesOrderService;
        this.salesOrderLineService = salesOrderLineService;
        this.customerService = customerService;
        this.itemService = itemService;
    }

    /**
     * GET /sales-order/new-sales-order
     *
     * Displays a form for creating a new sales order.
     *
     * @param salesOrder a SalesOrder object to be populated with form data
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = NEW_SALES_ORDER_PATH)
    public String newSalesOrder(@ModelAttribute SalesOrder salesOrder, Model model) {
        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "salesOrders/newSalesOrderForm";
    }

    /**
     * POST /sales-order/new-sales-order?addRow
     *
     * Adds a new sales order line to the sales order.
     *
     * @param salesOrder a SalesOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = NEW_SALES_ORDER_PATH, params = "addRow")
    public String addSaleOrderLine(@ModelAttribute SalesOrder salesOrder,
            HttpServletRequest request, Model model) {
        // Adding new sales order line to sales order
        salesOrder.getSaleOrderLines().add(new SalesOrderLine());

        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "salesOrders/newSalesOrderForm";
    }

    /**
     * POST /sales-order/new-sales-order?removeRow
     *
     * Removes a sales order line from the sales order.
     *
     * @param salesOrder a SalesOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = NEW_SALES_ORDER_PATH, params = "removeRow")
    public String removeSaleOrderLine(@ModelAttribute SalesOrder salesOrder,
            HttpServletRequest request, Model model) {
        // newSalesOrderDto.addOrderLine();
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        salesOrder.getSaleOrderLines().remove(rowId);

        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "salesOrders/newSalesOrderForm";
    }

    /**
     * POST /sales-order/new-sales-order?save
     *
     * Saves the sales order.
     *
     * @param salesOrder a SalesOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the sales orders
     *         list page if the sales order is saved successfully
     */
    @PostMapping(value = NEW_SALES_ORDER_PATH, params = "save")
    public String saveSaleOrder(@ModelAttribute SalesOrder salesOrder,
            HttpServletRequest request, Model model) {
        salesOrderService.save(salesOrder);
        // Redirect to the sales orders list page if the sales order is saved
        // successfully.
        return "redirect:/sales-orders?added";
    }

    /**
     * GET /sales-order
     *
     * Gets all sales orders.
     *
     * @param model a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = SALES_ORDER_PATH)
    public String getAllSalesOrders(Model model) {
        // TODO: -> Add pagination (25 records per page)
        // Add the title, customers, and sales orders to the model.
        model.addAttribute("title", "Sales Orders");
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("salesOrders", salesOrderService.findAllSalesOrder());
        // Return the name of the view to render.
        return "salesOrders/salesOrders";
    }

    /**
     * GET /sales-order
     *
     * Gets all sales orders.
     *
     * @param model a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @GetMapping(value = PENDING_SALES_ORDER_PATH)
    public String getAllPendingSalesOrders(Model model) {
        // TODO: -> Add pagination (25 records per page)
        // Add the title, customers, and sales orders to the model.
        model.addAttribute("title", "Sales Orders");
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("salesOrders", salesOrderService.findAllPendingOrders());
        // Return the name of the view to render.
        return "salesOrders/salesOrders";
    }

    /**
     * GET /sales-order/{orderId}
     *
     * Gets the sales order details for the given sales order ID.
     *
     * @param orderId the sales order ID
     * @param model   a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the sales orders
     *         list page if the sales order is not found
     */
    @GetMapping(value = SALES_ORDER_ID_PATH)
    public String getSalesOrderDetails(@PathVariable Long orderId, Model model) {
        Optional<SalesOrder> order = salesOrderService.findById(orderId);
        // If the sales order is found, add it to the model and return the name of the
        // view to render.
        if (order.isPresent()) {
            model.addAttribute("title", "Sales Orders");
            model.addAttribute("salesOrder", order.get());
            model.addAttribute("counter", new Counter());
            return "salesOrders/salesOrderDetails";
        } else {
            // The sales order is not found, redirect to the sales orders list page.
            return "redirect:/sales-orders?notFound";
        }
    }

     /**
     * GET /sales-order/{orderId}/update
     *
     * Gets the sales order update form for the given sales order ID.
     *
     * @param orderId the sales order ID
     * @param model   a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the sales orders
     *         list page if the sales order is not found
     */
    @GetMapping(value = UPDATE_SALES_ORDER_ID_PATH)
    public String getUpdateSalesOrderForm(@PathVariable Long orderId, Model model) {
        Optional<SalesOrder> order = salesOrderService.findById(orderId);
        // If the sales order is found, add it to the model and return the name of the
        // view to render.
        if (order.isPresent()) {
            model.addAttribute("title", "Sales Orders");
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("salesOrder", order.get());
            model.addAttribute("counter", new Counter());
            return "salesOrders/updateSalesOrderForm";
        } else {
            // The sales order is not found, redirect to the sales orders list page.
            return "redirect:/sales-orders?notFound";
        }
    }

    /**
     * POST /sales-order/{salesOrderId}/update?addRow
     *
     * Adds a new sales order line to the update sales order form.
     *
     * @param salesOrder a SalesOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = UPDATE_SALES_ORDER_ID_PATH, params = "addRow")
    public String addSaleOrderLineToUpdateOrderForm(@PathVariable("orderId") Long id,
            HttpServletRequest request, Model model) {
        // Adding new sales order line to sales order
        SalesOrder salesOrder = salesOrderService.findById(id).get();
        salesOrder.getSaleOrderLines().add(new SalesOrderLine());

        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "salesOrders/updateSalesOrderForm";
    }

    /**
     * POST /sales-order/{salesOrderId}/update?removeRow
     *
     * Removes a sales order line from the update sales order form.
     *
     * @param salesOrder a SalesOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render
     */
    @PostMapping(value = UPDATE_SALES_ORDER_ID_PATH, params = "removeRow")
    public String removeSaleOrderLineFromUpdateOrderForm(@PathVariable("orderId") Long id, 
            HttpServletRequest request, Model model) {
        SalesOrder salesOrder = salesOrderService.findById(id).get();
        // Checks if other was already shipped
        if (salesOrder.getStatus() == SoStatus.SHIPPED) {
            return "salesOrders/cannotBeUpdated";
        }
        final int rowId = Integer.valueOf(request.getParameter("removeRow"));
        // Delete Line
        SalesOrderLine deleteSalesOrderLine = salesOrder.getSaleOrderLines().get(rowId);
        salesOrder.getSaleOrderLines().remove(rowId);
        salesOrderLineService.delete(deleteSalesOrderLine);

        model.addAttribute("salesOrder", salesOrder);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("items", itemService.findAll());
        return "salesOrders/updateSalesOrderForm";
    }

    /**
     * POST /sales-order/{salesOrderId}/update?save
     *
     * Update an existing sales order by a given id.
     *
     * @param salesOrder a SalesOrder object to be populated with form data
     * @param request    an HttpServletRequest object
     * @param model      a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the sales orders
     *         list page if the sales order is saved successfully
     */
    @PostMapping(value = UPDATE_SALES_ORDER_ID_PATH, params = "save")
    public String updateSalesOrder(@PathVariable("orderId") Long id, @ModelAttribute SalesOrder salesOrder,
            HttpServletRequest request, Model model) {
        try {
            salesOrderService.updateById(id, salesOrder);
        } catch (SalesOrderNotFoundException e) {
            return "redirect:/sales-orders?notFound";
        } catch (ShippedOrderModificationException e) {
            return "redirect:/sales-orders?cannotBeUpdated";
        }
        // Redirect to the sales orders list page if the sales order was updated
        // successfully.
        return "redirect:/sales-orders?updated";
    }

    /**
     * POST /sales-order/{orderId}?delete
     *
     * Deletes the sales order with the given sales order ID.
     *
     * @param orderId the sales order ID
     * @param model   a Model object to be populated with data for the view
     * @return the name of the view to render, or a redirect to the sales orders
     *         list page if the sales order is not found or cannot be deleted
     */
    @PostMapping(value = SALES_ORDER_ID_PATH, params = "delete")
    public String deleteSalesOrder(@PathVariable Long orderId, Model model) {
        Optional<SalesOrder> order = salesOrderService.findById(orderId);
        // If the sales order is found and is not shipped, delete it and redirect to the
        // sales orders list page.
        if (order.isPresent() && order.get().getStatus() != SoStatus.SHIPPED) {
            salesOrderService.delete(order.get());
            return "redirect:/sales-orders?salesOrderDeleted";
        } else if (order.get().getStatus() == SoStatus.SHIPPED) {
            return "redirect:/sales-orders?failedToDelete";
        } else {
            return "redirect:/sales-orders/?notFound";
        }
    }

}
