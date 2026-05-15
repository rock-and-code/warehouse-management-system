package com.example.warehouseManagement.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNote.GrnStatus;

public interface GoodsReceiptNoteRepository
        extends JpaRepository<GoodsReceiptNote, Long>,
                JpaSpecificationExecutor<GoodsReceiptNote> {

    long countByStatus(GrnStatus status);

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
}
