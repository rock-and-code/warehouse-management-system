package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;

public interface GoodsReceiptNoteLineService {
    /**
     * Returns a list of goods receipt note lines by goods receipt note
     * @param goodsReceiptNote
     * @return
     */
    public List<GoodsReceiptNoteLine> findByGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote);
    /**
     * Saves a new goods receipt note line in the dba
     * @param goodsReceiptNoteLines
     * @return
     */
    public GoodsReceiptNoteLine save(GoodsReceiptNoteLine goodsReceiptNoteLines);
    /**
     * Deletes a goods receipt note line from the dba
     * @param goodsReceiptNoteLines
     */
    public void delete(GoodsReceiptNoteLine goodsReceiptNoteLines);
    /**
     * Deletes all the goods receipt note lines by a goods receipt note
     * @param goodsReceiptNote
     */
    public void deleteAllByGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote);
}
