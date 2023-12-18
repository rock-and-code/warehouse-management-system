package com.example.warehouseManagement.Domains.Exceptions;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException() {
        super("Customer Not Found");
    }
}
