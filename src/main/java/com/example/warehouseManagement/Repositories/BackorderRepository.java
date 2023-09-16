package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.DTOs.BackorderDto;


public interface BackorderRepository extends CrudRepository<Backorder, Long> {
    
    public List<Backorder> findBySalesOrder(SalesOrder salesOrder);

    @Query(value = "SELECT \n" +
        "so.date AS \"date\", \n" + 
        "customer.name AS \"customer\", \n" +
        "so.id AS \"salesOrder\", \n" +
        "ROUND(SUM(backorder.qty * item_price.price),2) AS \"total\", \n " +
        "FROM sales_order so \n" +
        "INNER JOIN backorder ON backorder.sales_order_id = so.id \n" +
        "INNER JOIN item_price ON backorder.item_id = item_price.id \n" +
        "INNER JOIN customer ON so.customer_id = customer.id \n" +
        "WHERE so.date LIKE CONCAT('',:year,'%') \n" +
        "GROUP BY so.id \n" +
        "ORDER BY \"date\"", nativeQuery = true)
    public List<BackorderDto> findBackordersByYear(@Param("year") int year);

    /**
    SELECT
    so.date AS "date",
    so.id AS "salesOrder",
    ROUND(SUM(backorder.qty * item_price.price),2) AS "total",
    FROM sales_order so
    INNER JOIN backorder ON backorder.sales_order_id = so.id
    INNER JOIN item_price ON backorder.item_id = item_price.id
    WHERE so.date LIKE CONCAT('',2023,'%')
    GROUP BY so.id
    ORDER BY "date"
     */
}
