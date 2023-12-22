package com.example.warehouseManagement.Domains.Exceptions;

public class ReceivedOrderModificationException extends Exception {
    public ReceivedOrderModificationException(String message) {
        super(message);
    }

    public ReceivedOrderModificationException() {
        super("Cannot modify a received order. Modifications are not allowed once an order has been received.");
    }
}
