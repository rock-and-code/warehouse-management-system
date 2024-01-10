package com.example.warehouseManagement.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;

public interface GoodsReceiptNoteRepository extends CrudRepository<GoodsReceiptNote, Long> {

    @Query(value = """
        SELECT 
            goods_receipt_note.id AS "id",
            goods_receipt_note.purchase_order_id AS "purchaseOrder",
            goods_receipt_note.date AS "date",
            goods_receipt_note.status AS "status"
        FROM 
            goods_receipt_note
            INNER JOIN purchase_order on goods_receipt_note.purchase_order_id = purchase_order.id
        WHERE 
            purchase_order.id = :purchaseOrderNumber
        """, nativeQuery = true)
    Optional<GoodsReceiptNote> findByPurchaseOrder(@Param("purchaseOrderNumber") Long purchaseOrderNumber);


    @Query(value = """
        SELECT
            grn.date AS "date",
            purchase_order.id AS "purchaseOrder",
            grn.id AS "goodsReceiptNote"
        FROM 
            goods_receipt_note grn
            INNER JOIN purchase_order ON purchase_order.id = grn.purchase_order_id
            INNER JOIN vendor ON purchase_order.vendor_id = vendor.id
        WHERE 
            purchase_order.vendor_id = :vendorId
            AND purchase_order.status = 0
        """, nativeQuery = true)
    List<GoodsReceiptNote> findAllPendingByVendor(@Param("vendorId") Long vendorId);
        // SELECT * FROM goods_receipt_note
        // INNER JOIN vendor on goods_receipt_note.vendor_id = vendor.id
        // WHERE goods_receipt_note.vendor_id = :vendorId;

    @Query(value = """
        SELECT 
            * 
        FROM 
            goods_receipt_note grn 
        WHERE 
            grn.status = 0
        ORDER BY
            grn.date ASC
        """, nativeQuery = true)
    List<GoodsReceiptNote> findAllPending();

    @Query(value = """
        SELECT 
            * 
        FROM 
            goods_receipt_note grn 
        WHERE 
            grn.status = 1
        ORDER BY
            grn.date ASC
        """, nativeQuery = true)
    List<GoodsReceiptNote> findAllFulfilled();
    
}
