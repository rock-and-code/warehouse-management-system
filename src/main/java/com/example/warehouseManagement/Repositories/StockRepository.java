package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.DTOs.StockLevelReportItemDto;
import com.example.warehouseManagement.Domains.DTOs.TopFiveMoversDto;

public interface StockRepository extends CrudRepository<Stock, Long> {

  /**
   * SELECT
   * item.sku AS "sku",
   * item.description AS "description",
   * NVL(SUM(stock.qty_on_hand),0) AS "qtyOnHand",
   * SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE()) AS
   * "averageWeeklySales",
   * NVL(ROUND(CAST(SUM(stock.qty_on_hand) AS REAL) / (SUM(sales_order_line.qty) /
   * EXTRACT(WEEK FROM CURRENT_DATE())), 2),0) AS "weeksOfInventory"
   * FROM sales_order so
   * INNER JOIN sales_order_line
   * ON sales_order_line.sales_order_id = so.id
   * INNER JOIN item
   * ON sales_order_line.item_id = item.id
   * LEFT JOIN stock
   * ON stock.item_id = item.id
   * GROUP BY item.id
   * ORDER by "averageWeeklySales" DESC
   * 
   * 
   * SELECT TOP 5
   * item.description as "description",
   * SUM(sales_order_line.qty) as "qty sold",
   * NVL((SELECT SUM(stock.qty_on_hand) FROM stock WHERE stock.item_id =
   * item.id),0) as "qty on hand",
   * FROM sales_order so
   * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
   * INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
   * INNER JOIN item ON sales_order_line.item_id = item.id
   * GROUP BY item.id
   * ORDER BY "qty sold" DESC
   * 
   * SELECT TOP 5
   * item.description as "description",
   * SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE()) as "average
   * week sales",
   * NVL((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id),0) as
   * "qty on hand",
   * NVL(ROUND((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id)
   * / CAST((SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())) AS
   * REAL),2),0) as "weeks of inventory"
   * FROM sales_order so
   * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
   * INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
   * INNER JOIN item ON sales_order_line.item_id = item.id
   * GROUP BY item.id
   * ORDER BY "average week sales" DESC
   * 
   * 
   * 
   */
  @Modifying
  @Transactional
  @Query(value = """
      SELECT TOP 5
          item.sku AS \"sku\",
          item.description as \"description\",
          SUM(sales_order_line.qty) / NULLIF(EXTRACT(WEEK FROM CURRENT_DATE()), 0) as \"averageWeekSales\",
          NVL((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id), 0) as \"qtyOnHand\",
          NVL(
              ROUND(
                  (SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id) /
                  NULLIF(CAST((SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())) AS REAL), 0),
                  2
              ),
              0
          ) as \"weeksOfInventory\"
      FROM sales_order so
      INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
      INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
      INNER JOIN item ON sales_order_line.item_id = item.id
      GROUP BY item.id
      ORDER BY \"averageWeekSales\" DESC, \"qtyOnHand\" DESC, \"sku\" DESC
            """, nativeQuery = true)
  public List<TopFiveMoversDto> getTopFiveMovers();
  // @Modifying
  // @Transactional
  // @Query(value = """
  // SELECT *
  // FROM (
  // SELECT
  // item.sku AS "sku",
  // item.description as "description",
  // SUM(sales_order_line.qty) / NULLIF(EXTRACT(WEEK FROM CURRENT_DATE()), 0) as
  // "averageWeekSales",
  // NVL((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id), 0) as
  // "qtyOnHand",
  // NVL(
  // ROUND(
  // (SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id) /
  // NULLIF(CAST((SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE()))
  // AS REAL), 0),
  // 2
  // ),
  // 0
  // ) as "weeksOfInventory",
  // DENSE_RANK() OVER (ORDER BY SUM(sales_order_line.qty) / NULLIF(EXTRACT(WEEK
  // FROM CURRENT_DATE()), 0) DESC) as "Rank"
  // FROM sales_order so
  // INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
  // INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
  // INNER JOIN item ON sales_order_line.item_id = item.id
  // GROUP BY item.id
  // ) RankedResults
  // ORDER BY "Rank" DESC, "qtyOnHand" DESC
  // LIMIT 5
  // """, nativeQuery = true)
  // public List<TopFiveMoversDto> getTopFiveMovers() throws
  // DataIntegrityViolationException;
  // @Modifying
  // @Transactional
  // @Query(value = """
  // SELECT TOP 5
  // item.sku AS "sku",
  // item.description as "description",
  // SUM(sales_order_line.qty) / NULLIF(EXTRACT(WEEK FROM CURRENT_DATE()), 0) as
  // "averageWeekSales",
  // NVL((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id), 0) as
  // "qtyOnHand",
  // NVL(
  // ROUND(
  // (SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = item.id) /
  // NULLIF(CAST((SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE()))
  // AS REAL), 0),
  // 2
  // ),
  // 0
  // ) as "weeksOfInventory"
  // FROM sales_order so
  // INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
  // INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
  // INNER JOIN item ON sales_order_line.item_id = item.id
  // GROUP BY item.id ORDER BY "averageWeekSales" DESC;
  // """, nativeQuery = true)
  // public List<TopFiveMoversDto> getTopFiveMovers() throws
  // DataIntegrityViolationException;

  // @Query(value = "SELECT \n" +
  // "s.id, \n" +
  // "s.warehouse_section_id, \n" +
  // "s.item_id, \n" +
  // "s.qty_on_hand \n" +
  // "FROM stock s\n" + //
  // "WHERE s.qty_on_hand <= :qty\n" + //
  // "AND s.item_id = :itemId")
  // public List<Stock> findByItemIdAndQty(@Param("itemId") Long itemId,
  // @Param("qty") int qty);

  public List<Stock> findByItem(Item item);

  @Query(value = """
      SELECT
            item.vendor_id AS \"vendorId\",
            item.sku as \"sku\",
            item.description as \"description\",
            -- AVERAGE WEEKS SALES NESTED QUERY
            (SELECT
             SUM(sales_order_line.qty) /  EXTRACT(WEEK FROM CURRENT_DATE())
             FROM sales_order so
             INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
             INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
             INNER JOIN item i ON sales_order_line.item_id = i.id
             WHERE i.vendor_id = :vendorId
             AND i.id = item.id
             GROUP BY i.id) as \"averageWeekSales\",
             NVL(SUM(s.qty_on_hand),0) as \"qtyOnHand\",
            -- WEEKS OF INVENTORY NESTED QUERY
            (SELECT
             NVL(ROUND((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = i.id) / CAST((SUM(sales_order_line.qty) /  EXTRACT(WEEK FROM CURRENT_DATE())) AS REAL),2),0)
             FROM sales_order so
             INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
             INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
             INNER JOIN item i ON sales_order_line.item_id = i.id
             WHERE i.vendor_id = :vendorId
             AND i.id = item.id
             GROUP BY i.id) as \"qtyOnHandWOI\",
            -- QTY IN TRANSIT NESTED QUERY
            (SELECT
             SUM(purchase_order_line.qty)
             FROM purchase_order po
             INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id = po.id
             INNER JOIN item i ON purchase_order_line.item_id = i.id
             WHERE i.vendor_id = item.vendor_id
             AND po.status = 1
             AND i.id = item.id
             GROUP BY i.id) as \"qtyInTransit\",
            -- WEEKS OF INVENTORY NESTED QUERY
            ROUND(CAST((SELECT
             SUM(purchase_order_line.qty)
             FROM purchase_order po
             INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id = po.id
             INNER JOIN item i ON purchase_order_line.item_id = i.id
             WHERE i.vendor_id = item.vendor_id
             AND po.status = 1
             AND i.id = item.id
             GROUP BY i.id)AS REAL) / (SELECT
              SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())
              FROM sales_order so
              INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
              INNER JOIN item i ON i.id = sales_order_line.item_id
              WHERE i.id = item.id),2) as \"qtyInTransitWOI\"
          FROM stock s
          RIGHT JOIN item ON s.item_id = item.id
          WHERE item.vendor_id = :vendorId
          GROUP BY item.id""", nativeQuery = true)
  public List<StockLevelReportItemDto> findStockReportsItemsByVendorId(@Param("vendorId") Long vendorId);

  // @Query(value = "SELECT\n" + //
  // " item.vendor_id AS \"vendorId\",\n" + //
  // " item.sku as \"sku\",\n" + //
  // " item.description as \"description\",\n" + //
  // // AVERAGE WEEKS SALES NESTED QUERY
  // " (SELECT\n" + //
  // " SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE()) \n" + //
  // " FROM sales_order so\n" + //
  // " INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id\n" +
  // //
  // " INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id\n" +
  // //
  // " INNER JOIN item i ON sales_order_line.item_id = i.id\n" + //
  // " WHERE i.vendor_id = :vendorId\n" + //
  // " AND i.id = item.id\n" + //
  // " GROUP BY i.id) as \"averageWeekSales\",\n" + //
  // " NVL(SUM(s.qty_on_hand),0) as \"qtyOnHand\",\n" + //
  // // WEEKS OF INVENTORY NESTED QUERY
  // " (SELECT\n" + //
  // " NVL(ROUND((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = i.id) /
  // CAST((SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())) AS
  // REAL),2),0)\n"
  // + //
  // " FROM sales_order so\n" + //
  // " INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id\n" +
  // //
  // " INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id\n" +
  // //
  // " INNER JOIN item i ON sales_order_line.item_id = i.id\n" + //
  // " WHERE i.vendor_id = :vendorId\n" + //
  // " AND i.id = item.id\n" + //
  // " GROUP BY i.id) as \"qtyOnHandWOI\",\n" + //
  // // QTY IN TRANSIT NESTED QUERY
  // " (SELECT \n" + //
  // " SUM(purchase_order_line.qty)\n" + //
  // " FROM purchase_order po\n" + //
  // " INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
  // po.id\n" + //
  // " INNER JOIN item i ON purchase_order_line.item_id = i.id\n" + //
  // " WHERE i.vendor_id = item.vendor_id\n" + //
  // " AND po.status = 1\n" + //
  // " AND i.id = item.id\n" + //
  // " GROUP BY i.id) as \"qtyInTransit\",\n" + //
  // // WEEKS OF INVENTORY NESTED QUERY
  // " ROUND(CAST((SELECT \n" + //
  // " SUM(purchase_order_line.qty)\n" + //
  // " FROM purchase_order po\n" + //
  // " INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
  // po.id\n" + //
  // " INNER JOIN item i ON purchase_order_line.item_id = i.id\n" + //
  // " WHERE i.vendor_id = item.vendor_id\n" + //
  // " AND po.status = 1\n" + //
  // " AND i.id = item.id\n" + // po.status = 1 (IN-TRANSIT)
  // " GROUP BY i.id)AS REAL) / (SELECT\n" + //
  // " SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())\n" + //
  // " FROM sales_order so\n" + //
  // " INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id\n" +
  // //
  // " INNER JOIN item i ON i.id = sales_order_line.item_id\n" + //
  // " WHERE i.id = item.id),2) as \"qtyInTransitWOI\"\n" + //
  // " FROM stock s\n" + //
  // " RIGHT JOIN item ON s.item_id = item.id\n" + //
  // " WHERE item.vendor_id = :vendorId\n" + //
  // " GROUP BY item.id", nativeQuery = true)
  // public List<StockLevelReportItemDto>
  // findStockReportsItemsByVendorId(@Param("vendorId") Long vendorId);
  /*
   * SELECT
   * item.vendor_id AS \"vendorId\",
   * item.sku as \"sku\",
   * item.description as \"description\",
   * -- AVERAGE WEEKS SALES NESTED QUERY
   * (SELECT
   * SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())
   * FROM sales_order so
   * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
   * INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
   * INNER JOIN item i ON sales_order_line.item_id = i.id
   * WHERE i.vendor_id = :vendorId
   * AND i.id = item.id
   * GROUP BY i.id) as \"averageWeekSales\",
   * NVL(SUM(s.qty_on_hand),0) as \"qtyOnHand\",
   * -- WEEKS OF INVENTORY NESTED QUERY
   * (SELECT
   * NVL(ROUND((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = i.id) /
   * CAST((SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())) AS
   * REAL),2),0)
   * FROM sales_order so
   * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
   * INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
   * INNER JOIN item i ON sales_order_line.item_id = i.id
   * WHERE i.vendor_id = :vendorId
   * AND i.id = item.id
   * GROUP BY i.id) as \"qtyOnHandWOI\",
   * -- QTY IN TRANSIT NESTED QUERY
   * (SELECT
   * SUM(purchase_order_line.qty)
   * FROM purchase_order po
   * INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
   * po.id
   * INNER JOIN item i ON purchase_order_line.item_id = i.id
   * WHERE i.vendor_id = item.vendor_id
   * AND po.status = 1
   * AND i.id = item.id
   * GROUP BY i.id) as \"qtyInTransit\",
   * -- WEEKS OF INVENTORY NESTED QUERY
   * ROUND(CAST((SELECT
   * SUM(purchase_order_line.qty)
   * FROM purchase_order po
   * INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
   * po.id
   * INNER JOIN item i ON purchase_order_line.item_id = i.id
   * WHERE i.vendor_id = item.vendor_id
   * AND po.status = 1
   * AND i.id = item.id
   * GROUP BY i.id)AS REAL) / (SELECT
   * SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())
   * FROM sales_order so
   * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
   * INNER JOIN item i ON i.id = sales_order_line.item_id
   * WHERE i.id = item.id),2) as \"qtyInTransitWOI\"
   * FROM stock s
   * RIGHT JOIN item ON s.item_id = item.id
   * WHERE item.vendor_id = :vendorId
   * GROUP BY item.id
   */
  @Query(value = """
      SELECT TOP 1 * FROM STOCK s\s
      WHERE s.item_id = :itemId AND s.warehouse_section_id = :warehouseSectionId
      ORDER by s.qty_on_hand DESC\
      """, nativeQuery = true)
  public Stock findByWarehouseSectionAndItemId(@Param("warehouseSectionId") Long warehouseSectionId,
      @Param("itemId") Long itemId);

  @Transactional
  @Modifying
  @Query(value = """
      UPDATE STOCK s\s
      SET s.qty_on_hand = :newQtyOnHand\s
      WHERE s.warehouse_section_id = :warehouseSectionId AND s.item_id = :itemId\
      """, nativeQuery = true)
  public void updateStockByWarehouseSectionAndItemId(@Param("newQtyOnHand") int newQtyOnHand,
      @Param("warehouseSectionId") Long warehouseSectionId, @Param("itemId") Long itemId);

  @Transactional
  @Modifying
  @Query(value = """
      DELETE STOCK s\s
      WHERE s.warehouse_section_id = :warehouseSectionId AND s.item_id = :itemId\
      """, nativeQuery = true)
  public void deleteStockByWarehouseSectionAndItemId(@Param("warehouseSectionId") Long warehouseSectionId,
      @Param("itemId") Long itemId);

  /**
   * 
   * DELETE STOCK s
   * WHERE s.item_id = 10 AND s.warehouse_section_id = 734
   * 
   * SELECT * FROM STOCK s
   * WHERE s.item_id = 10 AND s.warehouse_section_id = 734
   * 
   * UPDATE STOCK s
   * SET s.qty_on_hand = 1
   * WHERE s.warehouse_section_id 734 AND s.item_id = 10;
   * 
   * 
   * 
   * 
   */

  /**
   * SQL STATEMENT FOR STOCK LEVELS BY VENDOR
   * SELECT
   * item.vendor_id AS "Vendor",
   * item.sku as "Sku",
   * item.description as "Description",
   * SUM(s.qty_on_hand) as "Qty on Hand"
   * FROM stock s
   * INNER JOIN item ON s.item_id = item.id
   * WHERE item.vendor_id = 3
   * GROUP BY item.id
   */

  /**
   * SQL STATEMENT FOR QTY IN-TRANSIT BY VENDOR
   * SELECT
   * item.sku as "Sku",
   * item.description as "Description",
   * SUM(purchase_order_line.qty) as "In transit"
   * FROM purchase_order po
   * INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
   * po.id
   * INNER JOIN item ON purchase_order_line.item_id = item.id
   * WHERE item.vendor_id = 3
   * AND po.status = 1
   * GROUP BY item.id
   */

  /**
   * SQL STATEMENT FOR STOCK LEVELS WITH IN TRANSIT AMOUNT
   * SELECT
   * item.vendor_id AS "Vendor",
   * item.sku as "Sku",
   * item.description as "Description",
   * SUM(s.qty_on_hand) as "Qty on Hand",
   * (SELECT
   * SUM(purchase_order_line.qty)
   * FROM purchase_order po
   * INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
   * po.id
   * INNER JOIN item it ON purchase_order_line.item_id = it.id
   * WHERE it.vendor_id = item.vendor_id
   * AND po.status = 1
   * AND it.id = item.id
   * GROUP BY it.id) as "In transit"
   * FROM stock s
   * INNER JOIN item ON s.item_id = item.id
   * WHERE item.vendor_id = 3
   * GROUP BY item.id
   * 
   * 
   * SQL STATEMENT FOR STOCK LEVELS WITH IN TRANSIT AMOUNT AND AVERAGE WEEK SALES
   * SELECT
   * item.vendor_id AS "Vendor",
   * item.sku as "Sku",
   * item.description as "Description",
   * (SELECT
   * SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())
   * FROM sales_order so
   * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
   * INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
   * INNER JOIN item i ON sales_order_line.item_id = i.id
   * WHERE i.vendor_id = 1
   * AND i.id = item.id
   * GROUP BY i.id) as "averageWeekSales",
   * SUM(s.qty_on_hand) as "Qty on Hand",
   * (SELECT
   * SUM(purchase_order_line.qty)
   * FROM purchase_order po
   * INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
   * po.id
   * INNER JOIN item it ON purchase_order_line.item_id = it.id
   * WHERE it.vendor_id = item.vendor_id
   * AND po.status = 1
   * AND it.id = item.id
   * GROUP BY it.id) as "In transit"
   * FROM stock s
   * INNER JOIN item ON s.item_id = item.id
   * WHERE item.vendor_id = 1
   * GROUP BY item.id
   * 
   * 
   * 
   * 
   * 
   */

}

/*
 * 
 * --TOP 5 MOVERS AND WEEKS OF INVENTORY
 * 
 * SET @weeks = EXTRACT(WEEK FROM CURRENT_DATE());
 * SELECT TOP 5
 * item.sku AS "Sku",
 * item.description AS "Description",
 * SUM(s.qty_on_hand) AS "Qty On Hand",
 * SUM(sales_order_line.qty) / @weeks AS "Average Weekly Sales",
 * SUM(s.qty_on_hand) / (SUM(sales_order_line.qty) / @weeks) AS
 * "Weeks of  Inventory"
 * FROM Stock s
 * INNER JOIN item
 * ON s.item_id = item.id
 * INNER JOIN sales_order_line
 * ON sales_order_line.item_id = item.id
 * INNER JOIN sales_order
 * ON sales_order_line.sales_order_id = sales_order.id
 * GROUP BY item.id
 * ORDER by "Average Weekly Sales" DESC
 * 
 * 
 * 
 * 
 * --STOCK LEVELS REPORT SQL QUERY
 * SET @weeks = EXTRACT(WEEK FROM CURRENT_DATE());
 * SELECT
 * vendor.id AS "Vendor",
 * item.sku AS "Sku",
 * item.description AS "Description",
 * SUM(s.qty_on_hand) AS "Qty On Hand",
 * SUM(sales_order_line.qty) / @weeks AS "Average Weekly Sales",
 * SUM(s.qty_on_hand) / (SUM(sales_order_line.qty) / @weeks) AS
 * "Weeks of  Inventory"
 * FROM Stock s
 * INNER JOIN item
 * ON s.item_id = item.id
 * INNER JOIN Vendor
 * ON item.vendor_id = vendor.id
 * INNER JOIN sales_order_line
 * ON sales_order_line.item_id = item.id
 * INNER JOIN sales_order
 * ON sales_order_line.sales_order_id = sales_order.id
 * GROUP BY item.id
 * 
 * 
 */

/**
 * STOCK LEVEL REPORT -- FINAL
 * 
 * SELECT
 * item.vendor_id AS "vendor",
 * item.sku as "sku",
 * item.description as "description",
 * (SELECT
 * SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())
 * FROM sales_order so
 * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
 * INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
 * INNER JOIN item i ON sales_order_line.item_id = i.id
 * WHERE i.vendor_id = 1
 * AND i.id = item.id
 * GROUP BY i.id) as "averageWeekSales",
 * SUM(s.qty_on_hand) as "qtyOnHand",
 * (SELECT
 * NVL(ROUND((SELECT SUM(s.qty_on_hand) FROM stock s WHERE s.item_id = i.id) /
 * CAST((SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())) AS
 * REAL),2),0)
 * FROM sales_order so
 * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
 * INNER JOIN item_price ON sales_order_line.item_id = item_price.item_id
 * INNER JOIN item i ON sales_order_line.item_id = i.id
 * WHERE i.vendor_id = 1
 * AND i.id = item.id
 * GROUP BY i.id) as "weeksOfInventory",
 * (SELECT
 * SUM(purchase_order_line.qty)
 * FROM purchase_order po
 * INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
 * po.id
 * INNER JOIN item i ON purchase_order_line.item_id = i.id
 * WHERE i.vendor_id = item.vendor_id
 * AND po.status = 1
 * AND i.id = item.id
 * GROUP BY i.id) as "inTransit",
 * ROUND(CAST((SELECT
 * SUM(purchase_order_line.qty)
 * FROM purchase_order po
 * INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id =
 * po.id
 * INNER JOIN item i ON purchase_order_line.item_id = i.id
 * WHERE i.vendor_id = item.vendor_id
 * AND po.status = 1
 * AND i.id = item.id
 * GROUP BY i.id)AS REAL) / (SELECT
 * SUM(sales_order_line.qty) / EXTRACT(WEEK FROM CURRENT_DATE())
 * FROM sales_order so
 * INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
 * INNER JOIN item i ON i.id = sales_order_line.item_id
 * WHERE i.id = item.id),2) as "intransitWeeksOfInventory"
 * FROM stock s
 * INNER JOIN item ON s.item_id = item.id
 * WHERE item.vendor_id = 1
 * GROUP BY item.id
 */