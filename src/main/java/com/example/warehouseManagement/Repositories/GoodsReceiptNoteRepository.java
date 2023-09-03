package com.example.warehouseManagement.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;

public interface GoodsReceiptNoteRepository extends CrudRepository<GoodsReceiptNote, Long> {

    @Query(value = "SELECT * FROM goods_receipt_note\n" + //
            "INNER JOIN purchase_order on goods_receipt_note.purchase_order_number = purchase_order.purchase_order_number \n" + //
            "WHERE purchase_order.purchase_order_number = :purchaseOrderNumber", nativeQuery = true)
    Optional<GoodsReceiptNote> findByPurchaseOrder(@Param("purchaseOrderNumber") int purchaseOrder);

    @Query(value = "SELECT * FROM goods_receipt_note\n" + //
            "INNER JOIN vendor on goods_receipt_note.vendor_id = vendor.id \n" + //
            "WHERE goods_receipt_note.vendor_id = :vendorId", nativeQuery = true)
    List<GoodsReceiptNote> findAllByVendor(@Param("vendorId") Long vendorId);
        // SELECT * FROM goods_receipt_note
        // INNER JOIN vendor on goods_receipt_note.vendor_id = vendor.id
        // WHERE goods_receipt_note.vendor_id = :vendorId;
    
}
