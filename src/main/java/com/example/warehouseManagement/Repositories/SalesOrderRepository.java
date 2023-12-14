package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.DTOs.MonthlySalesDto;
import com.example.warehouseManagement.Domains.DTOs.PendingSalesOrderDto;
import com.example.warehouseManagement.Domains.DTOs.SalesOrderDto;


public interface SalesOrderRepository extends CrudRepository<SalesOrder, Long>{
    
    /**
     * Returns a list of sales order by a customer
     * @param customer
     * @return
     */
    public List<SalesOrder> findByCustomer(Customer customer);
    /**
     * Returns a list of the sales order with its total amount by a customer
     * @param customer
     * @return
     */
    @Query(value = """
      SELECT\s
      sales_order.date AS "date", sales_order.id AS "salesOrder",\s
      ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total"\s
      FROM customer AS C\s
      INNER JOIN sales_order ON sales_order.customer_id = C.id\s
      INNER JOIN sales_order_line ON sales_order_line.sales_order_id = sales_order.id\s
      INNER JOIN item_price ON item_price.id = sales_order_line.item_price_id\s
      WHERE C.id = :customerId\s
      GROUP BY sales_order.id\s
      ORDER BY sales_order.date DESC\
      """, nativeQuery = true)
    public List<SalesOrderDto> findAllByCustomer(@Param("customerId") Long customerId);

    /**
     * Returns a list of the sales order with its total amount by a customer
     * @param customer
     * @return
     */
    @Query(value = """
      SELECT\s
      so.id AS "id",\s
      so.date AS "date",\s
      so.id AS "salesOrder",\s
      ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total"\s
      FROM sales_order AS so\s
      INNER JOIN sales_order_line ON so.id = sales_order_line.sales_order_id\s
      INNER JOIN item_price ON item_price.id = sales_order_line.item_price_id\s
      GROUP BY so.id\s
      ORDER BY so.date\
      """, nativeQuery = true)
    public List<SalesOrderDto> findAllSalesOrder();
    
    /**
     * Returns a list of all the pending to ship sales order in the dba
     * @return
     */
    @Query(value = """
      SELECT\s
      so.date as "date",\s
      so.id as "salesOrderNumber",\s
      ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total"\s
      FROM sales_order AS so\s
      INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id\s
      INNER JOIN item_price ON sales_order_line.item_price_id = item_price.id\s
      WHERE so.status = 0\s
      GROUP BY so.id\s
      ORDER BY so.date DESC\
      """, nativeQuery = true)
    public List<SalesOrder> findAllPendingToShipOrders();
    /**
     * Returns a list of all the pending to ship sales order in the dba that matches a given year
     * @return
     */
    @Query(value = """
      SELECT\s
      TO_CHAR(so.date, 'Mon dd, yyyy') AS "date",\s
      customer.name as "customer",\s
      so.id AS "salesOrder",\s
      ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total",\s
      DATEDIFF(DAY,CURRENT_DATE(), date) AS "delay"\s
      FROM SALES_ORDER as so\s
      INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id\s
      INNER JOIN item_price ON sales_order_line.item_price_id = item_price.id\s
      INNER JOIN customer on so.customer_id = customer.id\s
      WHERE so.status = 0 AND so.date LIKE CONCAT(:year, '%')\s
      GROUP BY so.id\s
      ORDER BY "date" ASC\
      """, nativeQuery = true)
    public List<SalesOrder> findAllPendingToShipOrdersByYear(@Param("year") int year);

    /**
     * Returns a list of all the pending to ship sales order in the dba that matches a given year and month
     * @return
     */
    @Query(value = """
      SELECT\s
      TO_CHAR(so.date, 'Mon dd, yyyy') AS "date",\s
      customer.name as "customer",\s
      so.id AS "salesOrder",\s
      ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total",\s
      DATEDIFF(DAY,CURRENT_DATE(), date) AS "delay"\s
      FROM SALES_ORDER as so\s
      INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id\s
      INNER JOIN item_price ON sales_order_line.item_price_id = item_price.id\s
      INNER JOIN customer on so.customer_id = customer.id\s
      WHERE so.status = 0 AND so.date LIKE CONCAT(:year, '-', :month, '-', '0', '%')\s
      GROUP BY so.id\s
      ORDER BY "date" ASC\
      """, nativeQuery = true)
            //SELECT * FROM SALES_ORDER AS so WHERE so.status = 0 AND DATE LIKE '2023%';
    public List<PendingSalesOrderDto> findAllPendingToShipOrdersByYearAndMonth(@Param("year") int year,
         @Param("month") String month);

    @Query(value = """
        SELECT\s
        FORMATDATETIME(sales_order.date, 'MMM') AS "Month",\s
        ROUND(SUM(sol.qty * item_price.price),2) as "Total"\s
        FROM sales_order_line sol\s
        INNER JOIN sales_order ON sol.sales_order_id = sales_order.id\s
        INNER JOIN item_price ON sol.item_price_id = item_price.id\s
        WHERE EXTRACT(MONTH FROM Date) >= EXTRACT(MONTH FROM CURRENT_DATE()) - 3\s
        AND EXTRACT(MONTH FROM Date) < EXTRACT(MONTH FROM CURRENT_DATE())\s
        GROUP BY FORMATDATETIME(sales_order.date, 'MMM')\s
        ORDER BY FORMATDATETIME(sales_order.date, 'MMM') ASC\
        """, nativeQuery = true)
    public List<MonthlySalesDto> findLastThreeMonthsSales();
}

/*
 * SQL STATEMENT FOR SALES OF THE PAST THREE MONTHS
 
  SELECT
   FORMATDATETIME(sales_order.date, 'MMM') AS "Date",
   ROUND(SUM(sol.qty * product.sale_price),2) as "Total"
FROM sales_order_line sol
INNER JOIN sales_order
   ON sol.sales_order_id = sales_order.id
INNER JOIN product
  ON sol.product_id = product.id
WHERE Date >= CURRENT_DATE() - 91 AND Date < CURRENT_DATE() - 1
GROUP BY FORMATDATETIME(sales_order.date, 'MMM')
ORDER BY FORMATDATETIME(sales_order.date, 'MMM') ASC
 
 SELECT
   FORMATDATETIME(sales_order.date, 'MMM') AS "Date",
   ROUND(SUM(sol.qty * product.sale_price),2) as "Total"
FROM sales_order_line sol
INNER JOIN sales_order
   ON sol.sales_order_id = sales_order.id
INNER JOIN product
  ON sol.product_id = product.id
WHERE Date BETWEEN CURRENT_DATE() - 91 AND CURRENT_DATE() - 1
GROUP BY FORMATDATETIME(sales_order.date, 'MMM')
ORDER BY FORMATDATETIME(sales_order.date, 'MMM') DESC


 SELECT
   EXTRACT(MONTH FROM sales_order.date) AS "Date",
   ROUND(SUM(sol.qty * product.sale_price),2) as "Total"
FROM sales_order_line sol
INNER JOIN sales_order
   ON sol.sales_order_id = sales_order.id
INNER JOIN product
  ON sol.product_id = product.id
WHERE Date BETWEEN CURRENT_DATE() - 91 AND CURRENT_DATE() - 1
GROUP BY EXTRACT(MONTH FROM sales_order.date) 
ORDER BY EXTRACT(MONTH FROM sales_order.date)  DESC


 */


/**
 * SELECT
so.date AS "date",
customer.name as "customer",
so.sales_order_number AS "sale order",
ROUND(SUM(sales_order_line.qty * product.sale_price),2) AS "total",
DATEDIFF(DAY,CURRENT_DATE(), date) AS "delay"
FROM SALES_ORDER as so
INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
INNER JOIN product ON sales_order_line.product_id = product.id
INNER JOIN customer on so.customer_id = customer.id
WHERE so.status = 0 AND so.date LIKE '2023-06%'
GROUP BY so.sales_order_number
ORDER BY "date" ASC


--TOP 5 MOVERS SQL STATEMENT
SELECT TOP 5
  product.sku AS "Sku",
  product.description AS "Description",
  SUM(sol.qty) AS "Qty Sold"
FROM sales_order_line sol
INNER JOIN sales_order
  ON sol.sales_order_id = sales_order.id
INNER JOIN product
  ON sol.product_id = product.id
WHERE sales_order.date >= CURRENT_DATE() - 90
AND sales_order.date < CURRENT_DATE()
GROUP BY product.id
ORDER BY "Qty Sold" DESC




 */