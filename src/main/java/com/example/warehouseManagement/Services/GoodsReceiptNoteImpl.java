package com.example.warehouseManagement.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNote.GrnStatus;
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrder.PoStatus;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteDto;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteLineDto;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteLineRepository;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteRepository;
import com.example.warehouseManagement.Repositories.StockRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

@Service
public class GoodsReceiptNoteImpl implements GoodsReceiptNoteService {
    private final String FLOOR_WH_SECTION = "00-00-0-0";
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
    public Optional<GoodsReceiptNote> findByPurchaseOrder(Long purchaseOrderNumber) {
        return goodsReceiptNoteRepository.findByPurchaseOrder(purchaseOrderNumber);
    }

    /**
     * Returns a list of all the goods receipt notes by a given vendor
     */
    @Override
    public List<GoodsReceiptNote> findAllPendingByVendor(Long vendorId) {
        return goodsReceiptNoteRepository.findAllPendingByVendor(vendorId);
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
    public GoodsReceiptNote fulfill(GoodsReceiptNote goodsReceiptNote, GoodsReceiptNoteDto goodsReceiptNoteDto) {
        PurchaseOrder purchaseOrder = goodsReceiptNote.getPurchaseOrder();
        int purchaseOrderLines = purchaseOrder.getPurchaseOrderLines().size();
        int receivedPurchaseOrderLines = 0;
        // Usually the receiving department receive items to a area designated, generally, as the floor to 
        // then move the goods to designated areas in the warehouse (put away process)
        Optional<WarehouseSection> floor = warehouseSectionRepository.findBySectionNumber(FLOOR_WH_SECTION);
        // Create a new good
        for (int i=0; i<goodsReceiptNote.getGoodsReceiptNoteLines().size(); i++) {
            GoodsReceiptNoteLineDto goodsReceiptNoteLineDto = goodsReceiptNoteDto.getGoodsReceiptNoteLines().get(i);
            GoodsReceiptNoteLine goodsReceiptNoteLine = goodsReceiptNote.getGoodsReceiptNoteLines().get(i);
            int qty = goodsReceiptNoteLineDto.getQty();
            String notes = goodsReceiptNoteLineDto.getNotes();
            Item item = goodsReceiptNoteLine.getItem();
            if (qty == 0) continue;
            goodsReceiptNoteLine.setNotes(notes);
            Stock stock = Stock.builder().qtyOnHand(qty).item(item).warehouseSection(floor.get()).build();
            stockRepository.save(stock);
            goodsReceiptNoteLineRepository.save(goodsReceiptNoteLine);
            receivedPurchaseOrderLines++;
        }
        if (receivedPurchaseOrderLines == 0) return null;

        else if (receivedPurchaseOrderLines < purchaseOrderLines) {
            purchaseOrder.setStatus(PoStatus.PARTIALLY_RECEIVED);
        }
        purchaseOrder.setStatus(PoStatus.RECEIVED);
        goodsReceiptNote.setStatus(GrnStatus.FULFILLED);
        return goodsReceiptNoteRepository.save(goodsReceiptNote);
    }

    @Override
    public List<GoodsReceiptNote> findAllPending() {
        return goodsReceiptNoteRepository.findAllPending();
    }
    
    @Override
    public List<GoodsReceiptNote> findAllFulfilled() {
        return goodsReceiptNoteRepository.findAllFulfilled();
    }

    @Override
    public GoodsReceiptNote create(PurchaseOrder purchaseOrder) {
        GoodsReceiptNote goodsReceiptNote = GoodsReceiptNote.builder()
                    .purchaseOrder(purchaseOrder).build();
            // Create and persist a new Good receipt note
            GoodsReceiptNote savedGoodsReceiptNote = goodsReceiptNoteRepository.save(goodsReceiptNote);
            // Create and persist the goods receipt note lines from po
            for (PurchaseOrderLine purchaseOrderLine : purchaseOrder.getPurchaseOrderLines()) {
                // where each po line items will be stored
                GoodsReceiptNoteLine newGoodsReceiptNoteLine = GoodsReceiptNoteLine.builder()
                        .goodsReceiptNote(savedGoodsReceiptNote)
                        .item(purchaseOrderLine.getItem())
                        .qty(0).build();
                GoodsReceiptNoteLine saveGoodsReceiptNoteLine = goodsReceiptNoteLineRepository.save(newGoodsReceiptNoteLine);
                goodsReceiptNote.getGoodsReceiptNoteLines().add(saveGoodsReceiptNoteLine);
            }
        return goodsReceiptNoteRepository.save(savedGoodsReceiptNote);
    }

    public GoodsReceiptNoteDto addGoodReceiptNoteLines(GoodsReceiptNote goodsReceiptNote) {
        GoodsReceiptNoteDto goodsReceiptNoteDto = GoodsReceiptNoteDto.builder()
            .date(LocalDate.now())
            .purchaseOrderId(goodsReceiptNote.getPurchaseOrder().getId()).build();
        for (int i=0; i<goodsReceiptNote.getGoodsReceiptNoteLines().size(); i++) {
            goodsReceiptNoteDto.getGoodsReceiptNoteLines().add(new GoodsReceiptNoteLineDto());
        }
        return goodsReceiptNoteDto;
    }
    
}
