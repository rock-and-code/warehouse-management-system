package com.example.warehouseManagement.Domains.Exceptions;

public class PurchaseOrderNotFoundException extends Exception {
    public PurchaseOrderNotFoundException(String message) {
        super(message);
    }

    public PurchaseOrderNotFoundException() {
        super("Purchase Order Not Found");
    }
}
