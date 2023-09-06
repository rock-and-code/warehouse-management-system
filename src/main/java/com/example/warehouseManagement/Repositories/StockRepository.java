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
            "  item.sku AS \"sku\",\n" + //
            "  item.description AS \"description\",\n" + //
            "  SUM(s.qty_on_hand) AS \"qtyOnHand\",\n" + //
            "  SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE()) AS \"averageWeeklySales\",\n" + //
            "  ROUND(CAST(SUM(s.qty_on_hand) AS REAL) / (SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())), 2) AS \"weeksOfInventory\"\n" + //
            "FROM Stock s\n" + //
            "INNER JOIN item\n" + //
            "  ON s.item_id = item.id\n" + //
            "INNER JOIN sales_order_line\n" + //
            "  ON sales_order_line.item_id = item.id\n" + //
            "INNER JOIN sales_order\n" + //
            "  ON sales_order_line.sales_order_id = sales_order.id\n" + //
            "GROUP BY item.id\n" + //
            "ORDER by \"averageWeeklySales\" DESC", nativeQuery = true)
    public List<TopFiveMoversDto> getTopFiveMovers();
    
}

/*

--TOP 5 MOVERS AND WEEKS OF INVENTORY

SET @weeks = EXTRACT(WEEK FROM CURRENT_DATE());
SELECT TOP 5
  item.sku AS "Sku",
  item.description AS "Description",
  SUM(s.qty_on_hand) AS "Qty On Hand",
 SUM(sales_order_line.qty) / @weeks AS "Average Weekly Sales",
  SUM(s.qty_on_hand) / (SUM(sales_order_line.qty) / @weeks) AS "Weeks of  Inventory"
FROM Stock s
INNER JOIN item
  ON s.item_id = item.id
INNER JOIN sales_order_line
  ON sales_order_line.item_id = item.id
INNER JOIN sales_order
  ON sales_order_line.sales_order_id = sales_order.id
GROUP BY item.id
ORDER by "Average Weekly Sales" DESC




--STOCK LEVELS REPORT SQL QUERY
SET @weeks = EXTRACT(WEEK FROM CURRENT_DATE());
SELECT
  vendor.id AS "Vendor",
  item.sku AS "Sku",
  item.description AS "Description",
  SUM(s.qty_on_hand) AS "Qty On Hand",
 SUM(sales_order_line.qty) / @weeks AS "Average Weekly Sales",
  SUM(s.qty_on_hand) / (SUM(sales_order_line.qty) / @weeks) AS "Weeks of  Inventory"
FROM Stock s
INNER JOIN item
  ON s.item_id = item.id
INNER JOIN Vendor
  ON item.vendor_id = vendor.id
INNER JOIN sales_order_line
  ON sales_order_line.item_id = item.id
INNER JOIN sales_order
  ON sales_order_line.sales_order_id = sales_order.id
GROUP BY item.id


 */