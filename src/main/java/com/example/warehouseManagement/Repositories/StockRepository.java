package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.DTOs.TopFiveMoversDto;



public interface StockRepository extends CrudRepository<Stock, Long> {
    @Modifying
    @Transactional
    @Query(value= "SELECT TOP 5\n" + //
            "  product.sku AS \"sku\",\n" + //
            "  product.description AS \"description\",\n" + //
            "  SUM(s.qty_on_hand) AS \"qtyOnHand\",\n" + //
            "  SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE()) AS \"averageWeeklySales\",\n" + //
            "  ROUND(CAST(SUM(s.qty_on_hand) AS REAL) / (SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())), 2) AS \"weeksOfInventory\"\n" + //
            "FROM Stock s\n" + //
            "INNER JOIN Product\n" + //
            "  ON s.product_id = product.id\n" + //
            "INNER JOIN sales_order_line\n" + //
            "  ON sales_order_line.product_id = product.id\n" + //
            "INNER JOIN sales_order\n" + //
            "  ON sales_order_line.sales_order_id = sales_order.id\n" + //
            "GROUP BY product.id\n" + //
            "ORDER by \"averageWeeklySales\" DESC", nativeQuery = true)
    public List<TopFiveMoversDto> getTopFiveMovers();
    
}

/*

--TOP 5 MOVERS AND WEEKS OF INVENTORY

SET @weeks = EXTRACT(WEEK FROM CURRENT_DATE());
SELECT TOP 5
  product.sku AS "Sku",
  product.description AS "Description",
  SUM(s.qty_on_hand) AS "Qty On Hand",
 SUM(sales_order_line.qty) / @weeks AS "Average Weekly Sales",
  SUM(s.qty_on_hand) / (SUM(sales_order_line.qty) / @weeks) AS "Weeks of  Inventory"
FROM Stock s
INNER JOIN Product
  ON s.product_id = product.id
INNER JOIN sales_order_line
  ON sales_order_line.product_id = product.id
INNER JOIN sales_order
  ON sales_order_line.sales_order_id = sales_order.id
GROUP BY product.id
ORDER by "Average Weekly Sales" DESC




--STOCK LEVELS REPORT SQL QUERY
SET @weeks = EXTRACT(WEEK FROM CURRENT_DATE());
SELECT
  vendor.id AS "Vendor",
  product.sku AS "Sku",
  product.description AS "Description",
  SUM(s.qty_on_hand) AS "Qty On Hand",
 SUM(sales_order_line.qty) / @weeks AS "Average Weekly Sales",
  SUM(s.qty_on_hand) / (SUM(sales_order_line.qty) / @weeks) AS "Weeks of  Inventory"
FROM Stock s
INNER JOIN Product
  ON s.product_id = product.id
INNER JOIN Vendor
  ON product.vendor_id = vendor.id
INNER JOIN sales_order_line
  ON sales_order_line.product_id = product.id
INNER JOIN sales_order
  ON sales_order_line.sales_order_id = sales_order.id
GROUP BY product.id


 */