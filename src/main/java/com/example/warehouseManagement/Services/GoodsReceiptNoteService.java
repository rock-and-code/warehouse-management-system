package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.DTOs.AdvancedGrnSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteDto;

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
    public Optional<GoodsReceiptNote> findByPurchaseOrder(Long purchaseOrderNumber);
    /**
     * Returns a list of Goods receipt notes by a given vendor
     * @param vendor
     * @return
     */
    public List<GoodsReceiptNote> findAllPendingByVendor(Long vendorId);
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

    /**
     * Advanced search — empty criteria returns every row, paginated. Spec
     * returns cb.conjunction() when nothing is set.
     */
    Page<GoodsReceiptNote> findAdvanced(AdvancedGrnSearchCriteria criteria, Pageable pageable);

    public GoodsReceiptNote fulfill(GoodsReceiptNote goodsReceiptNote, GoodsReceiptNoteDto goodsReceiptNoteDto);

    public GoodsReceiptNote create(PurchaseOrder purchaseOrder);

    public GoodsReceiptNoteDto addGoodReceiptNoteLines(GoodsReceiptNote goodsReceiptNote);

    /** Dashboard KPI: count of pending GRNs. */
    long countPending();
}

