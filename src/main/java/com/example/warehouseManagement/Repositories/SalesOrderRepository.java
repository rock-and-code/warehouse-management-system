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
     * Returns a sales order by its sales order number
     * @param salesOrderNumber
     * @return
     */
    public SalesOrder findBySalesOrderNumber(int salesOrderNumber);
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
    @Query(value = "SELECT \n" + //
            "sales_order.date AS \"date\", sales_order.sales_order_number AS \"salesOrder\", \n" + //
            "ROUND(SUM(sales_order_line.qty * product.sale_price),2)AS \"total\"\n" + //
            "FROM customer AS C\n" + //
            "INNER JOIN sales_order ON sales_order.customer_id = C.id\n" + //
            "INNER JOIN sales_order_line ON sales_order_line.sales_order_id = sales_order.id\n" + //
            "INNER JOIN product ON product.id = sales_order_line.product_id\n" + //
            "WHERE C.id = :customerId\n" + //
            "GROUP BY sales_order.sales_order_number\n" + //
            "ORDER BY sales_order.date DESC", nativeQuery = true)
    public List<SalesOrderDto> findAllByCustomer(@Param("customerId") Long customerId);

    /**
     * Returns a list of the sales order with its total amount by a customer
     * @param customer
     * @return
     */
    @Query(value = "SELECT \n" +
        "so.id AS \"id\", " +
        "so.date AS \"date\", \n" +
        "so.sales_order_number AS \"salesOrder\", \n" + 
        "ROUND(SUM(sales_order_line.qty * product.sale_price),2) AS total \n" + 
        "FROM sales_order AS so \n" + 
        "INNER JOIN sales_order_line ON so.id = sales_order_line.sales_order_id \n" +
        "INNER JOIN product ON product.id = sales_order_line.product_id \n" +
        "GROUP BY so.sales_order_number \n" +
        "ORDER BY so.date DESC", nativeQuery = true)
    public List<SalesOrderDto> findAllSalesOrder();
    
    /**
     * Returns a list of all the pending to ship sales order in the dba
     * @return
     */
    @Query(value = "SELECT * \n" + //
            "FROM sales_order AS so\n" + //
            "WHERE so.status = 0\n" + //
            "ORDER BY so.date DESC", nativeQuery = true)
    public List<SalesOrder> findAllPendingToShipOrders();
    /**
     * Returns a list of all the pending to ship sales order in the dba that matches a given year
     * @return
     */
    @Query(value = "SELECT \n" +
    "TO_CHAR(so.date, 'Mon dd, yyyy') AS \"date\", \n" + 
    "customer.name as \"customer\", \n" + 
    "so.sales_order_number AS \"salesOrder\", \n" + 
    "ROUND(SUM(sales_order_line.qty * product.sale_price),2) AS \"total\", \n" + 
    "DATEDIFF(DAY,CURRENT_DATE(), date) AS \"delay\" \n" +
    "FROM SALES_ORDER as so \n" + 
    "INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id \n" + 
    "INNER JOIN product ON sales_order_line.product_id = product.id \n" +
    "INNER JOIN customer on so.customer_id = customer.id \n" + 
    "WHERE so.status = 0 AND so.date LIKE CONCAT(:year, '%') \n" + 
    "GROUP BY so.sales_order_number \n" + 
    "ORDER BY \"date\" ASC", nativeQuery = true)
            //SELECT * FROM SALES_ORDER AS so WHERE so.status = 0 AND DATE LIKE '2023%';
    public List<SalesOrder> findAllPendingToShipOrdersByYear(@Param("year") int year);

    /**
     * Returns a list of all the pending to ship sales order in the dba that matches a given year and month
     * @return
     */
    @Query(value = "SELECT \n" +
    "TO_CHAR(so.date, 'Mon dd, yyyy') AS \"date\", \n" + 
    "customer.name as \"customer\", \n" + 
    "so.sales_order_number AS \"salesOrder\", \n" + 
    "ROUND(SUM(sales_order_line.qty * product.sale_price),2) AS \"total\", \n" + 
    "DATEDIFF(DAY,CURRENT_DATE(), date) AS \"delay\" \n" +
    "FROM SALES_ORDER as so \n" + 
    "INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id \n" + 
    "INNER JOIN product ON sales_order_line.product_id = product.id \n" +
    "INNER JOIN customer on so.customer_id = customer.id \n" + 
    "WHERE so.status = 0 AND so.date LIKE CONCAT(:year, '-', :month, '%') \n" + 
    "GROUP BY so.sales_order_number \n" + 
    "ORDER BY \"date\" ASC", nativeQuery = true)
            //SELECT * FROM SALES_ORDER AS so WHERE so.status = 0 AND DATE LIKE '2023%';
    public List<PendingSalesOrderDto> findAllPendingToShipOrdersByYearAndMonth(@Param("year") int year,
         @Param("month") String month);

    @Query(value = "SELECT \n" +
        "FORMATDATETIME(sales_order.date, 'MMM') AS \"Month\", \n" +
        "ROUND(SUM(sol.qty * product.sale_price),2) as \"Total\" \n" +
        "FROM sales_order_line sol \n" + 
        "INNER JOIN sales_order \n" +
        "ON sol.sales_order_id = sales_order.id \n" +
        "INNER JOIN product \n" +
        "ON sol.product_id = product.id \n" + 
        "WHERE Date >= CURRENT_DATE() - 91 AND Date < CURRENT_DATE() - 1 \n" +
        "GROUP BY FORMATDATETIME(sales_order.date, 'MMM') \n" + 
        "ORDER BY FORMATDATETIME(sales_order.date, 'MMM') ASC ", nativeQuery = true)
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