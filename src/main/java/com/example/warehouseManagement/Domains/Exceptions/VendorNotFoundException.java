package com.example.warehouseManagement.Domains.Exceptions;

public class VendorNotFoundException extends Exception {
    public VendorNotFoundException(String message) {
        super(message);
    }

    public VendorNotFoundException() {
        super("Vendor Not Found");
    }
}
