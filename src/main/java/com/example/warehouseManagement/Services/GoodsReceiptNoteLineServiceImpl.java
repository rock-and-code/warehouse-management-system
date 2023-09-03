package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteLineRepository;

public class GoodsReceiptNoteLineServiceImpl implements GoodsReceiptNoteLineService {
    private final GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository;

    

    public GoodsReceiptNoteLineServiceImpl(GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository) {
        this.goodsReceiptNoteLineRepository = goodsReceiptNoteLineRepository;
    }

    @Override
    public List<GoodsReceiptNoteLine> findByGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote) {
        return goodsReceiptNoteLineRepository.findByGoodsReceiptNote(goodsReceiptNote);
    }

    @Override
    public GoodsReceiptNoteLine save(GoodsReceiptNoteLine goodsReceiptNoteLines) {
        return goodsReceiptNoteLineRepository.save(goodsReceiptNoteLines);
    }

    @Override
    public void delete(GoodsReceiptNoteLine goodsReceiptNoteLines) {
        goodsReceiptNoteLineRepository.delete(goodsReceiptNoteLines);
    }

    @Override
    public void deleteAllByGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote) {
        goodsReceiptNoteLineRepository.deleteAllByGoodsReceiptNote(goodsReceiptNote);
    }
    
}
