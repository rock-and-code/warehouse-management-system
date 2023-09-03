package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;

public interface GoodsReceiptNoteLineRepository extends CrudRepository<GoodsReceiptNoteLine, Long> {
    /**
     * Returns a list of goods receipt note lines by goods receipt note
     * @param goodsReceiptNote
     * @return
     */
    public List<GoodsReceiptNoteLine> findByGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote);
    /**
     * Deletes all the goods receipt note lines by a given goods receipt note
     * @param goodsReceiptNote
     */
    public void deleteAllByGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote);
}
