package com.example.warehouseManagement.Domains;

import java.util.ArrayList;
import java.util.List;

public class VendorPool {
    private VendorPool(){}
    public static List<Vendor> vendorList = List.of(
        Vendor.builder().name("Apple Inc.").street("One Apple Park Way").city("Cupertino").state("CA").zipcode(95014).contactInfo("800-692-7753").products(new ArrayList<>()).purchaseOrders(new ArrayList<>()).build(),
        Vendor.builder().name("Microsoft Inc.").street("1 Microsoft Way,").city("Redmond").state("WA").zipcode(98052).contactInfo("425-706-4400").products(new ArrayList<>()).purchaseOrders(new ArrayList<>()).build(),
        Vendor.builder().name("Alphabet Inc.").street("1600 Amphitheatre Parkway").city("Mountain View").state("CA").zipcode(94043).contactInfo("650-253-0000 ").products(new ArrayList<>()).purchaseOrders(new ArrayList<>()).build(),
        Vendor.builder().name("Amazon Inc.").street("410 Terry Ave N").city("Seattle").state("WA").zipcode(98109).contactInfo("206-266-1000").products(new ArrayList<>()).purchaseOrders(new ArrayList<>()).build(),
        Vendor.builder().name("Cisco Systems").street("170 West Tasman Dr.").city("San Jose").state("CA").zipcode(95134).contactInfo("408-526-4000").products(new ArrayList<>()).purchaseOrders(new ArrayList<>()).build()
    );
}
