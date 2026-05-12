package com.example.warehouseManagement.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.DTOs.AgingPoDto;
import com.example.warehouseManagement.Domains.DTOs.OpenOrdersKpiDto;
import com.example.warehouseManagement.Domains.DTOs.PoStatusBucketDto;
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderDto;
import com.example.warehouseManagement.Domains.DTOs.VendorSpendDto;


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
        SELECT
            purchase_order.id AS "id",
            purchase_order.status AS "status",
            purchase_order.date AS "date", purchase_order.purchase_order_number AS "purchaseOrder",
            ROUND(SUM(purchase_order_line.qty * item_cost.cost),2) AS "total"
        FROM 
            vendor AS V
            INNER JOIN purchase_order ON purchase_order.vendor_id = V.id
            INNER JOIN purchase_order_line ON purchase_order_line.purchase_order_id = purchase_order.id
            INNER JOIN item_cost ON item_cost.id = purchase_order_line.item_cost_id
        WHERE 
            V.id = :vendorId
        GROUP BY 
            purchase_order.purchase_order_number
        ORDER BY 
            purchase_order.date DESC
        """, nativeQuery = true)
    public List<PurchaseOrderDto> findAllByVendor(@Param("vendorId") Long vendorId);
    /**
     * @Query(value = "SELECT * FROM PURCHASEORDER WHERE PURCHASEORDER.vendor = :vendor", nativeQuery = true)
    public Optional<PurchaseOrder> findAllByVendor(@Param("vendor") int vendorId);
     */

    /**
     * Returns a list of the purchase order persisted in the dba
     * @return
     */
    @Query(value = """
        SELECT
            po.id AS "id",
            po.status AS "status",
            po.date AS "date",
            po.id AS "purchaseOrder",
            ROUND(SUM(purchase_order_line.qty * item_cost.cost),2) AS total
        FROM 
            purchase_order po
            INNER JOIN purchase_order_line ON po.id = purchase_order_line.purchase_order_id
            INNER JOIN item_cost ON item_cost.id = purchase_order_line.item_cost_id
        GROUP BY 
            po.id
        ORDER BY 
            po.date
    """, nativeQuery = true)
    public List<PurchaseOrderDto> findAllPurchaseOrder();

    /**
     * Returns a list of the purchase order persisted in the dba
     * @return
     */
    @Query(value = """
    SELECT
        po.id AS "id",
        po.status AS "status",
        po.date AS "date",
        po.id AS "purchaseOrder",
        ROUND(SUM(purchase_order_line.qty * item_cost.cost),2) AS total
    FROM 
        purchase_order po
        INNER JOIN purchase_order_line ON po.id = purchase_order_line.purchase_order_id
        INNER JOIN item_cost ON item_cost.id = purchase_order_line.item_cost_id
    WHERE 
        po.status = 0
    GROUP BY 
        po.id
    ORDER BY 
        po.date
    """, nativeQuery = true)
    public List<PurchaseOrderDto> findAllPendingPurchaseOrder();

    /**
     * Dashboard KPI: count + value of open purchase orders
     * (IN_TRANSIT = 0, PARTIALLY_RECEIVED = 2). RECEIVED (1) is closed.
     */
    @Query(value = """
      SELECT
        COUNT(DISTINCT po.id) AS "count",
        COALESCE(ROUND(SUM(pol.qty * ic.cost), 2), 0) AS "totalValue"
      FROM purchase_order po
      LEFT JOIN purchase_order_line pol ON pol.purchase_order_id = po.id
      LEFT JOIN item_cost ic ON ic.id = pol.item_cost_id
      WHERE po.status IN (0, 2)
      """, nativeQuery = true)
    public OpenOrdersKpiDto findOpenPurchaseOrdersKpi();

    /**
     * Top 5 vendors by YTD spend (sum of qty * cost across all POs raised in
     * the current calendar year).
     */
    @Query(value = """
      SELECT
        v.name AS "vendorName",
        ROUND(SUM(pol.qty * ic.cost), 2) AS "total"
      FROM purchase_order po
      INNER JOIN purchase_order_line pol ON pol.purchase_order_id = po.id
      INNER JOIN item_cost ic ON ic.id = pol.item_cost_id
      INNER JOIN vendor v ON v.id = po.vendor_id
      WHERE EXTRACT(YEAR FROM po.date) = EXTRACT(YEAR FROM CURRENT_DATE())
      GROUP BY v.id, v.name
      ORDER BY "total" DESC
      LIMIT 5
      """, nativeQuery = true)
    public List<VendorSpendDto> findTopVendorsBySpendYtd();

    /**
     * PO count grouped by status — feeds the doughnut chart.
     * 0 = IN_TRANSIT, 1 = RECEIVED, 2 = PARTIALLY_RECEIVED.
     */
    @Query(value = """
      SELECT
        po.status AS "status",
        COUNT(*) AS "count"
      FROM purchase_order po
      GROUP BY po.status
      ORDER BY po.status
      """, nativeQuery = true)
    public List<PoStatusBucketDto> findPoStatusBuckets();

    /**
     * POs older than 30 days that are still IN_TRANSIT (0) or PARTIALLY_RECEIVED (2).
     * Sorted oldest-first so the worst offenders are at the top.
     */
    @Query(value = """
      SELECT
        po.id AS "id",
        v.name AS "vendorName",
        po.date AS "date",
        DATEDIFF('DAY', po.date, CURRENT_DATE()) AS "ageDays",
        po.status AS "status",
        ROUND(SUM(pol.qty * ic.cost), 2) AS "total"
      FROM purchase_order po
      INNER JOIN purchase_order_line pol ON pol.purchase_order_id = po.id
      INNER JOIN item_cost ic ON ic.id = pol.item_cost_id
      INNER JOIN vendor v ON v.id = po.vendor_id
      WHERE po.status IN (0, 2)
        AND DATEDIFF('DAY', po.date, CURRENT_DATE()) > 30
      GROUP BY po.id, v.name, po.date, po.status
      ORDER BY "ageDays" DESC
      """, nativeQuery = true)
    public List<AgingPoDto> findAgingPurchaseOrders();
}
