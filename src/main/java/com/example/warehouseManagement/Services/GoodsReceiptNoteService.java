package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;

public interface GoodsReceiptNoteService {
    /**
     * Returns a Goods receipt note by a given id
     * @param id
     * @return
     */
    public Optional<GoodsReceiptNote> findById(Long id);
    /**
     * REturns a Goods receipt note that matches a purchase order
     * @param purchaseOrder
     * @return
     */
    public Optional<GoodsReceiptNote> findByPurchaseOrder(int purchaseOrder);
    /**
     * Returns a list of Goods receipt notes by a given vendor
     * @param vendor
     * @return
     */
    public List<GoodsReceiptNote> findAllByVendor(Long vendorId);
    /**
     * Returns all the Goods receipt notes persisted in the dba
     * @return
     */
    public Iterable<GoodsReceiptNote> findAll();
    /**
     * Persists a Goods receipt note in the dba
     * @param goodsReceiptNote
     * @return
     */
    public GoodsReceiptNote save(GoodsReceiptNote goodsReceiptNote);
    /**
     * Deletes a Goods receipt note from the DBA
     * @param goodsReceiptNote
     */
    public void delete(GoodsReceiptNote goodsReceiptNote);
}

