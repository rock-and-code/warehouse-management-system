package com.example.warehouseManagement.Domains;

import java.util.List;

import com.example.warehouseManagement.Domains.DTOs.ItemInfoDto;

public class ItemPool {
    private ItemPool(){}
    public static List<ItemInfoDto> ProductList = List.of(
        //0-7 APPLE PRODUCTS
        ItemInfoDto.builder().description("AirPods").salesPrice(169).cost(120).sku(100001).build(),
        ItemInfoDto.builder().description("AirPods Max").salesPrice(540).cost(450).sku(100002).build(),
        ItemInfoDto.builder().description("MacBook Pro 13").salesPrice(1299).cost(1000).sku(101000).build(),
        ItemInfoDto.builder().description("MacBook Pro 14").salesPrice(1999).cost(1750).sku(101001).build(),
        ItemInfoDto.builder().description("MacBook Pro 16").salesPrice(1999).cost(1750).sku(101002).build(),
        ItemInfoDto.builder().description("MacBook Air 13").salesPrice(1099).cost(800).sku(101010).build(),
        ItemInfoDto.builder().description("Ipad Pro").salesPrice(799).cost(500).sku(102001).build(),
        ItemInfoDto.builder().description("Iphone 14").salesPrice(999).cost(700).sku(103001).build(),
        //8-10 MICROSOFT PRODUCTS
        ItemInfoDto.builder().description("Surface Pro 8").salesPrice(799).cost(600).sku(200010).build(),
        ItemInfoDto.builder().description("Surface Laptop 4").salesPrice(700).cost(500).sku(201001).build(),
        ItemInfoDto.builder().description("Xbox Series X").salesPrice(499).cost(350).sku(203001).build(),
        //11-16 ALPABHET
        ItemInfoDto.builder().description("Wireless charger with fast charging capabilities").salesPrice(49.99).cost(39.99).sku(323456).build(),
        ItemInfoDto.builder().description("Portable bluetooth speaker with 10 hours of battery life").salesPrice(99.99).cost(79.99).sku(334567).build(),
        ItemInfoDto.builder().description("Smart home hub that controls your lights, thermostat, and other smart devices").salesPrice(199.99).cost(179.99).sku(345678).build(),
        ItemInfoDto.builder().description("GPS, heart rate monitor, and water resistance up to 50 meters").salesPrice(299.99).cost(249.99).sku(356789).build(),
        ItemInfoDto.builder().description("15.6 Laptop with Intel Core i5 processor, 8GB RAM, and 256GB SSD").salesPrice(999.99).cost(799.99).sku(367890).build(),
        ItemInfoDto.builder().description("1080p resolution, 110-degree field of view, and 90Hz refresh rate").salesPrice(499.99).cost(399.99).sku(378901).build(),
        //17-33 AMAZON
        ItemInfoDto.builder().description("6.2 AMOLED display, Snapdragon 888 processor, and 12GB RAM").salesPrice(1199.99).cost(999.99).sku(489012).build(),
        ItemInfoDto.builder().description("11 Retina display, A15 Bionic chip, and 64GB storage").salesPrice(799.99).cost(699.99).sku(490123).build(),
        ItemInfoDto.builder().description("Active noise cancellation, sweatproof design, and 5 hours of battery life").salesPrice(249.99).cost(199.99).sku(401234).build(),
        ItemInfoDto.builder().description("2TB external hard drive with USB-C connectivity").salesPrice(149.99).cost(119.99).sku(423456).build(),
        ItemInfoDto.builder().description("Full HD webcam with built-in microphone").salesPrice(59.99).cost(49.99).sku(423457).build(),
        ItemInfoDto.builder().description("Mechanical keyboard with RGB backlighting").salesPrice(99.99).cost(79.99).sku(434567).build(),
        ItemInfoDto.builder().description("Wireless mouse with 1000Hz polling rate").salesPrice(49.99).cost(39.99).sku(445678).build(),
        ItemInfoDto.builder().description("Over-ear headphones with noise cancellation").salesPrice(199.99).cost(179.99).sku(456789).build(),
        ItemInfoDto.builder().description("Condenser microphone with cardioid pickup pattern").salesPrice(99.99).cost(79.99).sku(467890).build(),
        ItemInfoDto.builder().description("3D printer with auto bed leveling and filament sensor").salesPrice(499.99).cost(399.99).sku(423456).build(),
        ItemInfoDto.builder().description("4K monitor with IPS panel and 144Hz refresh rate").salesPrice(599.99).cost(499.99).sku(434567).build(),
        ItemInfoDto.builder().description("17.3 gaming laptop with Intel Core i7 processor, 16GB RAM, and 1TB SSD").salesPrice(1499.99).cost(1299.99).sku(445678).build(),
        ItemInfoDto.builder().description("Wireless gaming headset with 7.1 surround sound").salesPrice(299.99).cost(249.99).sku(456789).build(),
        ItemInfoDto.builder().description("Wired gaming mouse with 12,000 DPI sensor").salesPrice(99.99).cost(79.99).sku(467890).build(),
        ItemInfoDto.builder().description("Mechanical gaming keyboard with RGB backlighting").salesPrice(149.99).cost(119.99).sku(478901).build(),
        ItemInfoDto.builder().description("Full HD webcam with built-in microphone").salesPrice(59.99).cost(49.99).sku(489012).build(),
        ItemInfoDto.builder().description("Mechanical keyboard with RGB backlighting").salesPrice(99.99).cost(79.99).sku(490123).build(),
        //34-40 CISCO
        ItemInfoDto.builder().description("Wireless mouse with 1000Hz polling rate").salesPrice(49.99).cost(39.99).sku(501234).build(),
        ItemInfoDto.builder().description("Over-ear headphones with noise cancellation").salesPrice(199.99).cost(179.99).sku(512345).build(),
        ItemInfoDto.builder().description("Condenser microphone with cardioid pickup pattern").salesPrice(99.99).cost(79.99).sku(523456).build(),
        ItemInfoDto.builder().description("2TB external hard drive with USB-C connectivity").salesPrice(149.99).cost(119.99).sku(534567).build(),
        ItemInfoDto.builder().description("Full HD webcam with built-in microphone").salesPrice(59.99).cost(49.99).sku(545678).build(),
        ItemInfoDto.builder().description("Mechanical keyboard with RGB backlighting").salesPrice(99.99).cost(79.99).sku(556789).build(),
        ItemInfoDto.builder().description("Wireless mouse with 1000Hz polling rate").salesPrice(49.99).cost(39.99).sku(567890).build()

    );
    
}
