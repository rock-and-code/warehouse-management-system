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
      SELECT
        sales_order.date AS "date", sales_order.id AS "salesOrder",
        ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total"
      FROM 
        customer AS C
        INNER JOIN sales_order ON sales_order.customer_id = C.id
        INNER JOIN sales_order_line ON sales_order_line.sales_order_id = sales_order.id
        INNER JOIN item_price ON item_price.id = sales_order_line.item_price_id
      WHERE 
        C.id = :customerId
      GROUP BY 
        sales_order.id
      ORDER BY 
        sales_order.date DESC
      """, nativeQuery = true)
    public List<SalesOrderDto> findAllByCustomer(@Param("customerId") Long customerId);

    /**
     * Returns a list of the sales order with its total amount by a customer
     * @param customer
     * @return
     */
    @Query(value = """
      SELECT
        so.id AS "id",
        so.status AS "status",
        so.date AS "date",
        so.id AS "salesOrder",
        ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total"
      FROM 
        sales_order AS so
        INNER JOIN sales_order_line ON so.id = sales_order_line.sales_order_id
        INNER JOIN item_price ON item_price.id = sales_order_line.item_price_id
      GROUP BY 
        so.id
      ORDER BY 
        so.date
      """, nativeQuery = true)
    public List<SalesOrderDto> findAllSalesOrder();
    
    /**
     * Returns a list of all the pending to ship sales order in the dba
     * @return
     */
    @Query(value = """
      SELECT
        so.date as "date",
        so.id as "salesOrderNumber",
        ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total"
      FROM 
        sales_order AS so
        INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
        INNER JOIN item_price ON sales_order_line.item_price_id = item_price.id
      WHERE 
        so.status = 0
      GROUP BY 
        so.id
      ORDER BY 
        so.date DESC
      """, nativeQuery = true)
    public List<SalesOrder> findAllPendingToShipOrders();
    /**
     * Returns a list of all the pending to ship sales order in the dba that matches a given year
     * @return
     */
    @Query(value = """
      SELECT
        TO_CHAR(so.date, 'Mon dd, yyyy') AS "date",
        customer.name as "customer",
        so.id AS "salesOrder",
        ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total",
        DATEDIFF(DAY,CURRENT_DATE(), date) AS "delay"
      FROM 
        SALES_ORDER as so
        INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
        INNER JOIN item_price ON sales_order_line.item_price_id = item_price.id
        INNER JOIN customer on so.customer_id = customer.id
      WHERE 
        so.status = 0 AND so.date LIKE CONCAT(:year, '%')
      GROUP BY 
        so.id
      ORDER BY 
        "date" ASC
      """, nativeQuery = true)
    public List<SalesOrder> findAllPendingToShipOrdersByYear(@Param("year") int year);

    /**
     * Returns a list of all the pending to ship sales order in the dba that matches a given year and month
     * @return
     */
    @Query(value = """
      SELECT
        TO_CHAR(so.date, 'Mon dd, yyyy') AS "date",
        customer.name AS "customer",
        so.id AS "salesOrder",
        ROUND(SUM(sales_order_line.qty * item_price.price), 2) AS "total",
        DATEDIFF('DAY', CURRENT_DATE(), so.date) AS "delay"
      FROM 
        SALES_ORDER AS so
        INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id
        INNER JOIN item_price ON sales_order_line.item_price_id = item_price.id
        INNER JOIN customer ON so.customer_id = customer.id
      WHERE 
        so.status = 0 
        AND EXTRACT(YEAR FROM so.date) = :year
        AND EXTRACT(MONTH FROM so.date) = :month
      GROUP BY 
        TO_CHAR(so.date, 'Mon dd, yyyy'),
        customer.name,
        so.id
      ORDER BY 
        "date" ASC;
      """, nativeQuery = true)
            //SELECT * FROM SALES_ORDER AS so WHERE so.status = 0 AND DATE LIKE '2023%';
    public List<PendingSalesOrderDto> findAllPendingToShipOrdersByYearAndMonth(@Param("year") int year,
         @Param("month") String month);

    @Query(value = """
      SELECT
        FORMATDATETIME(sales_order.date, 'MMM') AS "Month",
        ROUND(SUM(sol.qty * item_price.price), 2) AS "Total"
      FROM
        sales_order_line sol
        INNER JOIN sales_order ON sol.sales_order_id = sales_order.id
        INNER JOIN item_price ON sol.item_price_id = item_price.id
      WHERE
        sales_order.date >= DATEADD('MONTH', -3, CURRENT_DATE())
        AND sales_order.date < DATEADD('MONTH', 0, CURRENT_DATE())
      GROUP BY
        FORMATDATETIME(sales_order.date, 'MMM')
      ORDER BY
        "Month" DESC
        """, nativeQuery = true)
    public List<MonthlySalesDto> findLastThreeMonthsSales();

    /**
     * Returns a list of the sales order with its total amount by a customer
     * @param customer
     * @return
     */
    @Query(value = """
      SELECT
        so.id AS "id",
        so.status AS "status",
        so.date AS "date",
        so.id AS "salesOrder",
        ROUND(SUM(sales_order_line.qty * item_price.price),2) AS "total"
      FROM 
        sales_order AS so
        INNER JOIN sales_order_line ON so.id = sales_order_line.sales_order_id
        INNER JOIN item_price ON item_price.id = sales_order_line.item_price_id
      WHERE 
        so.status = 0
      GROUP BY 
        so.id
      ORDER BY 
        so.date
      """, nativeQuery = true)
    public List<SalesOrderDto> findAllPendingSalesOrder();
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