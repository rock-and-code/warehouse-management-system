package com.example.warehouseManagement.Domains;

import java.util.List;

public class ProductPool {
    private ProductPool(){}
    public static List<Product> ProductList = List.of(
        //0-7 APPLE PRODUCTS
        Product.builder().description("AirPods").salePrice(169).cost(120).sku(100001).build(),
        Product.builder().description("AirPods Max").salePrice(540).cost(450).sku(100002).build(),
        Product.builder().description("MacBook Pro 13").salePrice(1299).cost(1000).sku(101000).build(),
        Product.builder().description("MacBook Pro 14").salePrice(1999).cost(1750).sku(101001).build(),
        Product.builder().description("MacBook Pro 16").salePrice(1999).cost(1750).sku(101002).build(),
        Product.builder().description("MacBook Air 13").salePrice(1099).cost(800).sku(101010).build(),
        Product.builder().description("Ipad Pro").salePrice(799).cost(500).sku(102001).build(),
        Product.builder().description("Iphone 14").salePrice(999).cost(700).sku(103001).build(),
        //8-10 MICROSOFT PRODUCTS
        Product.builder().description("Surface Pro 8").salePrice(799).cost(600).sku(200010).build(),
        Product.builder().description("Surface Laptop 4").salePrice(700).cost(500).sku(201001).build(),
        Product.builder().description("Xbox Series X").salePrice(499).cost(350).sku(203001).build(),
        //11-16 ALPABHET
        Product.builder().description("Wireless charger with fast charging capabilities").salePrice(49.99).cost(39.99).sku(323456).build(),
        Product.builder().description("Portable bluetooth speaker with 10 hours of battery life").salePrice(99.99).cost(79.99).sku(334567).build(),
        Product.builder().description("Smart home hub that controls your lights, thermostat, and other smart devices").salePrice(199.99).cost(179.99).sku(345678).build(),
        Product.builder().description("GPS, heart rate monitor, and water resistance up to 50 meters").salePrice(299.99).cost(249.99).sku(356789).build(),
        Product.builder().description("15.6 Laptop with Intel Core i5 processor, 8GB RAM, and 256GB SSD").salePrice(999.99).cost(799.99).sku(367890).build(),
        Product.builder().description("1080p resolution, 110-degree field of view, and 90Hz refresh rate").salePrice(499.99).cost(399.99).sku(378901).build(),
        //17-33 AMAZON
        Product.builder().description("6.2 AMOLED display, Snapdragon 888 processor, and 12GB RAM").salePrice(1199.99).cost(999.99).sku(489012).build(),
        Product.builder().description("11 Retina display, A15 Bionic chip, and 64GB storage").salePrice(799.99).cost(699.99).sku(490123).build(),
        Product.builder().description("Active noise cancellation, sweatproof design, and 5 hours of battery life").salePrice(249.99).cost(199.99).sku(401234).build(),
        Product.builder().description("2TB external hard drive with USB-C connectivity").salePrice(149.99).cost(119.99).sku(423456).build(),
        Product.builder().description("Full HD webcam with built-in microphone").salePrice(59.99).cost(49.99).sku(423457).build(),
        Product.builder().description("Mechanical keyboard with RGB backlighting").salePrice(99.99).cost(79.99).sku(434567).build(),
        Product.builder().description("Wireless mouse with 1000Hz polling rate").salePrice(49.99).cost(39.99).sku(445678).build(),
        Product.builder().description("Over-ear headphones with noise cancellation").salePrice(199.99).cost(179.99).sku(456789).build(),
        Product.builder().description("Condenser microphone with cardioid pickup pattern").salePrice(99.99).cost(79.99).sku(467890).build(),
        Product.builder().description("3D printer with auto bed leveling and filament sensor").salePrice(499.99).cost(399.99).sku(423456).build(),
        Product.builder().description("4K monitor with IPS panel and 144Hz refresh rate").salePrice(599.99).cost(499.99).sku(434567).build(),
        Product.builder().description("17.3 gaming laptop with Intel Core i7 processor, 16GB RAM, and 1TB SSD").salePrice(1499.99).cost(1299.99).sku(445678).build(),
        Product.builder().description("Wireless gaming headset with 7.1 surround sound").salePrice(299.99).cost(249.99).sku(456789).build(),
        Product.builder().description("Wired gaming mouse with 12,000 DPI sensor").salePrice(99.99).cost(79.99).sku(467890).build(),
        Product.builder().description("Mechanical gaming keyboard with RGB backlighting").salePrice(149.99).cost(119.99).sku(478901).build(),
        Product.builder().description("Full HD webcam with built-in microphone").salePrice(59.99).cost(49.99).sku(489012).build(),
        Product.builder().description("Mechanical keyboard with RGB backlighting").salePrice(99.99).cost(79.99).sku(490123).build(),
        //34-40 CISCO
        Product.builder().description("Wireless mouse with 1000Hz polling rate").salePrice(49.99).cost(39.99).sku(501234).build(),
        Product.builder().description("Over-ear headphones with noise cancellation").salePrice(199.99).cost(179.99).sku(512345).build(),
        Product.builder().description("Condenser microphone with cardioid pickup pattern").salePrice(99.99).cost(79.99).sku(523456).build(),
        Product.builder().description("2TB external hard drive with USB-C connectivity").salePrice(149.99).cost(119.99).sku(534567).build(),
        Product.builder().description("Full HD webcam with built-in microphone").salePrice(59.99).cost(49.99).sku(545678).build(),
        Product.builder().description("Mechanical keyboard with RGB backlighting").salePrice(99.99).cost(79.99).sku(556789).build(),
        Product.builder().description("Wireless mouse with 1000Hz polling rate").salePrice(49.99).cost(39.99).sku(567890).build()

    );
    
}
