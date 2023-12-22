package com.example.warehouseManagement.Domains.Exceptions;

public class ShippedOrderModificationException extends Exception {
    public ShippedOrderModificationException(String message) {
        super(message);
    }

    public ShippedOrderModificationException() {
        super("Cannot modify a shipped order. Modifications are not allowed once an order has been shipped.");
    }
}
