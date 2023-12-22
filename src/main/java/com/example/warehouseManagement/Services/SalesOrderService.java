package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.LastThreeMonthsSales;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.DTOs.PendingSalesOrderDto;
import com.example.warehouseManagement.Domains.DTOs.SalesOrderDto;
import com.example.warehouseManagement.Domains.Exceptions.SalesOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ShippedOrderModificationException;

public interface SalesOrderService {
    /**
     * Returns a Sales Order persisted in the dba by a given id
     * @param id
     * @return
     */
    public Optional<SalesOrder> findById(Long id);
    /**
     * Returns a list of sales orders persisted in the dba by a given customer id
     * @param customer
     * @return
     */
    public List<SalesOrder> findByCustomer(Customer customer);
    /**
     * Returns a list of all pending sales orders persisted in the dba 
     * @return
     */
    public List<SalesOrderDto> findAllPendingOrders();
    /**
     * Returns a list of all the sales orders persisted in the dba \
     * @return
     */
    public Iterable<SalesOrder> findAll();
    /**
     * Returns a list of all the sales order placed by a customer in the dba by a given customer id
     * @param customerId
     * @return list of sales order
     */
    public List<SalesOrderDto> findAllByCustomer(Long customerId);

    /**
     * Returns a list of all the sales order pending to ship in the dba
     * @return list of sales order
     */
    public List<SalesOrder> findAllPendingToShipOrders();
    /**
     * Returns a list of all the pending to ship sales order by a given year and month
     * @param year
     * @param month
     * @return
     */
    public List<PendingSalesOrderDto> findAllPendingToShipOrdersByYearAndMonth(int year, String month);
    /**
     * Update an existing sales order in the db by its id
     * @param id
     * @param salesOrder
     * @return
     */
    public SalesOrder updateById(Long id, SalesOrder salesOrder) throws SalesOrderNotFoundException, ShippedOrderModificationException;
    /**
     * Persists a sales order in the dba
     * @param salesOrder
     * @return
     */
    public SalesOrder save(SalesOrder salesOrder);
    /**
     * Deletes a sales order from the dba
     * @param salesOrder
     */
    public void delete(SalesOrder salesOrder);
    /**
     * Returns a list of all the sales orders persisted in the dba
     * @return
     */
    public List<SalesOrderDto> findAllSalesOrder();
    /**
     * Returns a wrapper pojo that contains a list of the previous three months total sales
     * @return
     */
    public LastThreeMonthsSales findLastThreeMonthsSales();
}


/**
 * SELECT
so.date AS "date",
customer.name as "customer",
so.sales_order_number AS "salesOrder",
ROUND(SUM(sales_order_line.qty * product.sale_price),2) AS "total",
FROM SALES_ORDER as so
INNER JOIN sales_order_line ON sales_order_line.sales_order_id = so.id 
INNER JOIN product ON sales_order_line.product_id = product.id
INNER JOIN customer on so.customer_id = customer.id
WHERE so.status = 0 AND so.date LIKE '2023-06%'
GROUP BY so.sales_order_number
ORDER BY "total" DESC
 */