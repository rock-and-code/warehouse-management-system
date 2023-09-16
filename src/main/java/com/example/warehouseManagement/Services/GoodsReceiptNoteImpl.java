package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNote.GrnStatus;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteDto;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteLineRepository;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteRepository;
import com.example.warehouseManagement.Repositories.StockRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

@Service
public class GoodsReceiptNoteImpl implements GoodsReceiptNoteService {
    private final GoodsReceiptNoteRepository goodsReceiptNoteRepository;
    private final GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository;
    private final StockRepository stockRepository;
    private final WarehouseSectionRepository warehouseSectionRepository;
    
    
    public GoodsReceiptNoteImpl(GoodsReceiptNoteRepository goodsReceiptNoteRepository,
            GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository,
            StockRepository stockRepository, WarehouseSectionRepository warehouseSectionRepository) {
        this.goodsReceiptNoteRepository = goodsReceiptNoteRepository;
        this.goodsReceiptNoteLineRepository = goodsReceiptNoteLineRepository;
        this.stockRepository = stockRepository;
        this.warehouseSectionRepository = warehouseSectionRepository;
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
        GoodsReceiptNote savedGoodsReceiptNote = goodsReceiptNoteRepository.save(goodsReceiptNote);
        goodsReceiptNoteLineRepository.saveAll(savedGoodsReceiptNote.getGoodsReceiptNoteLines());
        return goodsReceiptNoteRepository.save(savedGoodsReceiptNote);
    }

    /**
     * Deletes a given goods receipt note from the dba
     */
    @Override
    public void delete(GoodsReceiptNote goodsReceiptNote) {
        goodsReceiptNoteRepository.delete(goodsReceiptNote);
    }

    @Override
    public void fulfill(GoodsReceiptNote goodsReceiptNote, GoodsReceiptNoteDto goodsReceiptNoteDto) {
        goodsReceiptNote.setStatus(GrnStatus.RECEIVED);
        for (int i=0; i<goodsReceiptNoteDto.getGoodsReceiptNoteLines().size(); i++) {
            int qty = goodsReceiptNoteDto.getGoodsReceiptNoteLines().get(i).getQty();
            Long warehouseSectionId = goodsReceiptNoteDto.getGoodsReceiptNoteLines().get(i).getWarehouseSectionId();
            Optional<WarehouseSection> warehouseSection = warehouseSectionRepository.findById(warehouseSectionId);
            Item item = goodsReceiptNote.getGoodsReceiptNoteLines().get(i).getItem();
            //TODO: ADD LOGIC TO HANDLE DAMAGES (ASSIGN A WAREHOUSE SECTION FOR DAMAGES PRODUCTS)
            //boolean damaged = (goodsReceiptNoteDto.getGoodsReceiptNoteLines().get(i).getDamaged() == 0) ? false : true;
            Stock stock = Stock.builder().qtyOnHand(qty).item(item).warehouseSection(warehouseSection.get()).build();
            stockRepository.save(stock);
        }
        goodsReceiptNoteRepository.save(goodsReceiptNote);
    }

    @Override
    public List<GoodsReceiptNote> findAllPending() {
        return goodsReceiptNoteRepository.findAllPending();
    }
    
}
