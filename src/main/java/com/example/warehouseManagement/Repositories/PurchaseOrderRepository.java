package com.example.warehouseManagement.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderDto;


public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Long>{
    /**
     * Return a Purchase Order that matches the given purchase order number
     * @param purchaseOrderNumber
     * @return
     */
    @Query(value = "SELECT * FROM purchase_order WHERE purchase_order.purchase_order_number = :purchaseOrderNumber", nativeQuery = true)
    public Optional<PurchaseOrder> findByPurchaseOrderNumber(@Param("purchaseOrderNumber") int purchaseOrderNumber);

    /**
     * Return a list of all the purchase order by a given vendor
     * @param vendor
     * @return
     */
    @Query(value = """
        SELECT\s
        purchase_order.id AS "id",\s
        purchase_order.date AS "date", purchase_order.purchase_order_number AS "purchaseOrder",\s
        ROUND(SUM(purchase_order_line.qty * item_cost.cost),2) AS "total"\s
        FROM vendor AS V\s
        INNER JOIN purchase_order ON purchase_order.vendor_id = V.id\s
        INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id = purchase_order.id\s
        INNER JOIN item_cost ON item_cost.id = purchase_order_line.item_cost_id\s
        WHERE V.id = :vendorId \
        GROUP BY purchase_order.purchase_order_number\s
        ORDER BY purchase_order.date DESC\
        """, nativeQuery = true)
    public List<PurchaseOrderDto> findAllByVendor(@Param("vendorId") Long vendorId);
    /**
     * @Query(value = "SELECT * FROM PURCHASEORDER WHERE PURCHASEORDER.vendor = :vendor", nativeQuery = true)
    public Optional<PurchaseOrder> findAllByVendor(@Param("vendor") int vendorId);
     */

    /**
     * Returns a list of the purchase order persisted in the dba
     * @param customer
     * @return
     */
    @Query(value = """
    SELECT\s
    po.id AS "id",\s
    po.date AS "date",\s
    po.id AS "purchaseOrder",\s
    ROUND(SUM(purchase_order_line.qty * item_cost.cost),2) AS total\s
    FROM purchase_order po\s
    INNER JOIN purchase_order_line ON po.id = purchase_order_line.purchase_order_id\s
    INNER JOIN item_cost ON item_cost.id = purchase_order_line.item_cost_id\s
    GROUP BY po.id\s
    ORDER BY po.date\
    """, nativeQuery = true)
    public List<PurchaseOrderDto> findAllPurchaseOrder();
    
}
