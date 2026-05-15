package com.example.warehouseManagement.Controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.warehouseManagement.Domains.Exceptions.CustomerNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ItemNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.PurchaseOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ReceivedOrderModificationException;
import com.example.warehouseManagement.Domains.Exceptions.SalesOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ShippedOrderModificationException;
import com.example.warehouseManagement.Domains.Exceptions.VendorNotFoundException;

/**
 * Centralizes the redirect-to-list-with-banner behavior that used to live as
 * try/catch blocks in every controller. Each handler returns a redirect string
 * that the existing list templates already render via {@code th:if="${param.X}"}.
 *
 * Generic HTTP errors (404 / 403 / 500) are handled by Spring Boot's
 * BasicErrorController via {@code templates/error/{status}.html} — no
 * @ExceptionHandler needed for those.
 *
 * AdminUserController's DuplicateUserException / UserNotFoundException remain
 * caught locally because they re-render the form with field-level errors, which
 * needs Model/BindingResult access that doesn't translate cleanly to a global
 * redirect handler.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public String handleCustomerNotFound() {
        return "redirect:/customers?notFound";
    }

    @ExceptionHandler(VendorNotFoundException.class)
    public String handleVendorNotFound() {
        return "redirect:/vendors?notFound";
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public String handleItemNotFound() {
        return "redirect:/items?notFound";
    }

    @ExceptionHandler(SalesOrderNotFoundException.class)
    public String handleSalesOrderNotFound() {
        return "redirect:/sales-orders?notFound";
    }

    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    public String handlePurchaseOrderNotFound() {
        return "redirect:/purchase-orders?notFound";
    }

    @ExceptionHandler(ShippedOrderModificationException.class)
    public String handleShippedOrderModification() {
        return "redirect:/sales-orders?cannotBeUpdated";
    }

    @ExceptionHandler(ReceivedOrderModificationException.class)
    public String handleReceivedOrderModification() {
        return "redirect:/purchase-orders?cannotBeUpdated";
    }
}
