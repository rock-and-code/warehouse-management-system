package com.example.warehouseManagement.Domains.Exceptions;

public class SalesOrderNotFoundException extends Exception {
    public SalesOrderNotFoundException(String message) {
        super(message);
    }

    public SalesOrderNotFoundException() {
        super("Sales Order Not Found");
    }
}
