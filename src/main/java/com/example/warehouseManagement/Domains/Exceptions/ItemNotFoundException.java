package com.example.warehouseManagement.Domains.Exceptions;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException() {
        super("Item Not Found");
    }
}
