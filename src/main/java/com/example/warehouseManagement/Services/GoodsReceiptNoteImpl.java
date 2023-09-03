package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteRepository;

@Service
public class GoodsReceiptNoteImpl implements GoodsReceiptNoteService {
    private final GoodsReceiptNoteRepository goodsReceiptNoteRepository;
    
    
    public GoodsReceiptNoteImpl(GoodsReceiptNoteRepository goodsReceiptNoteRepository) {
        this.goodsReceiptNoteRepository = goodsReceiptNoteRepository;
    }

    /**
     * Returns a goods receipt note by 
     */
    @Override
    public Optional<GoodsReceiptNote> findById(Long id) {
        return goodsReceiptNoteRepository.findById(id);
        
    }

    /**
     * Returns a Goods receipt note by a given purchase order number
     */
    @Override
    public Optional<GoodsReceiptNote> findByPurchaseOrder(int purchaseOrder) {
        return goodsReceiptNoteRepository.findByPurchaseOrder(purchaseOrder);
    }

    /**
     * Returns a list of all the goods receipt notes by a given vendor
     */
    @Override
    public List<GoodsReceiptNote> findAllByVendor(Long vendorId) {
        return goodsReceiptNoteRepository.findAllByVendor(vendorId);
    }

    /**
     * Returns a list of all goods receipt notes persisted in the dba
     */
    @Override
    public Iterable<GoodsReceiptNote> findAll() {
        return goodsReceiptNoteRepository.findAll();
    }

    /**
     * Persist a new Goods receipt note in the dba
     */
    @Override
    public GoodsReceiptNote save(GoodsReceiptNote goodsReceiptNote) {
        return goodsReceiptNoteRepository.save(goodsReceiptNote);
    }

    /**
     * Deletes a given goods receipt note from the dba
     */
    @Override
    public void delete(GoodsReceiptNote goodsReceiptNote) {
        goodsReceiptNoteRepository.delete(goodsReceiptNote);
    }
    
}
