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

    @Query(value = """
        SELECT\s
        so.date AS "date",\s
        customer.name AS "customer",\s
        so.id AS "salesOrder",\s
        ROUND(SUM(backorder.qty * item_price.price),2) AS "total",\s
         \
        FROM sales_order so\s
        INNER JOIN backorder ON backorder.sales_order_id = so.id\s
        INNER JOIN item_price ON backorder.item_id = item_price.id\s
        INNER JOIN customer ON so.customer_id = customer.id\s
        WHERE so.date LIKE CONCAT('',:year,'%')\s
        GROUP BY so.id\s
        ORDER BY "date"\
        """, nativeQuery = true)
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
