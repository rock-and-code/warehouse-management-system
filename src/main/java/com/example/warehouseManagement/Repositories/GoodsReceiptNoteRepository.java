package com.example.warehouseManagement.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;

public interface GoodsReceiptNoteRepository extends CrudRepository<GoodsReceiptNote, Long> {

    @Query(value = "SELECT * FROM goods_receipt_note\n" + //
            "INNER JOIN purchase_order on goods_receipt_note.purchase_order_id = purchase_order.id \n" + //
            "WHERE purchase_order.purchase_order_number = :purchaseOrderNumber", nativeQuery = true)
    Optional<GoodsReceiptNote> findByPurchaseOrder(@Param("purchaseOrderNumber") int purchaseOrder);

    @Query(value = "SELECT \n" +
        "grn.date AS \"date\", \n" +
        "purchase_order.purchase_order_number AS \"purchaseOrder\", \n" +
        "grn.id AS \"goodsReceiptNote\" \n" +
        "FROM goods_receipt_note grn \n" +
        "INNER JOIN purchase_order ON purchase_order.id = grn.purchase_order_id \n" + 
        "INNER JOIN vendor ON purchase_order.vendor_id = vendor.id \n" +
        "WHERE purchase_order.vendor_id = :vendorId", nativeQuery = true)
    List<GoodsReceiptNote> findAllByVendor(@Param("vendorId") Long vendorId);
        // SELECT * FROM goods_receipt_note
        // INNER JOIN vendor on goods_receipt_note.vendor_id = vendor.id
        // WHERE goods_receipt_note.vendor_id = :vendorId;
    
}
