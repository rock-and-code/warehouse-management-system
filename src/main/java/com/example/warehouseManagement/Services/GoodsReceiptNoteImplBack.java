// package com.example.warehouseManagement.Services;

// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;

// import com.example.warehouseManagement.Domains.GoodsReceiptNote;
// import com.example.warehouseManagement.Domains.GoodsReceiptNote.GrnStatus;
// import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;
// import com.example.warehouseManagement.Domains.Item;
// import com.example.warehouseManagement.Domains.PurchaseOrder;
// import com.example.warehouseManagement.Domains.Stock;
// import com.example.warehouseManagement.Domains.WarehouseSection;
// import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteDto;
// import com.example.warehouseManagement.Repositories.GoodsReceiptNoteLineRepository;
// import com.example.warehouseManagement.Repositories.GoodsReceiptNoteRepository;
// import com.example.warehouseManagement.Repositories.StockRepository;
// import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

// @Service
// public class GoodsReceiptNoteImplBack implements GoodsReceiptNoteService {
//     private final String DAMAGE_WH_SECTION = "11-11-1-1";
//     private final int NOT_FULFILLED = 0;
//     private final int PARTIALLY_FULFILLED = 1;
//     private final int FULFILLED = 2;
//     private final GoodsReceiptNoteRepository goodsReceiptNoteRepository;
//     private final GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository;
//     private final StockRepository stockRepository;
//     private final WarehouseSectionRepository warehouseSectionRepository;
    
    
//     public GoodsReceiptNoteImplBack(GoodsReceiptNoteRepository goodsReceiptNoteRepository,
//             GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository,
//             StockRepository stockRepository, WarehouseSectionRepository warehouseSectionRepository) { 
//         this.goodsReceiptNoteRepository = goodsReceiptNoteRepository;
//         this.goodsReceiptNoteLineRepository = goodsReceiptNoteLineRepository;
//         this.stockRepository = stockRepository;
//         this.warehouseSectionRepository = warehouseSectionRepository;
//     }

//     /**
//      * Returns a goods receipt note by 
//      */
//     @Override
//     public Optional<GoodsReceiptNote> findById(Long id) {
//         return goodsReceiptNoteRepository.findById(id);
        
//     }

//     /**
//      * Returns a Goods receipt note by a given purchase order number
//      */
//     @Override
//     public Optional<GoodsReceiptNote> findByPurchaseOrder(int purchaseOrder) {
//         return goodsReceiptNoteRepository.findByPurchaseOrder(purchaseOrder);
//     }

//     /**
//      * Returns a list of all the goods receipt notes by a given vendor
//      */
//     @Override
//     public List<GoodsReceiptNote> findAllPendingByVendor(Long vendorId) {
//         return goodsReceiptNoteRepository.findAllPendingByVendor(vendorId);
//     }

//     /**
//      * Returns a list of all goods receipt notes persisted in the dba
//      */
//     @Override
//     public Iterable<GoodsReceiptNote> findAll() {
//         return goodsReceiptNoteRepository.findAll();
//     }

//     /**
//      * Persist a new Goods receipt note in the dba
//      */
//     @Override
//     public GoodsReceiptNote save(GoodsReceiptNote goodsReceiptNote) {
//         GoodsReceiptNote savedGoodsReceiptNote = goodsReceiptNoteRepository.save(goodsReceiptNote);
//         goodsReceiptNoteLineRepository.saveAll(savedGoodsReceiptNote.getGoodsReceiptNoteLines());
//         return goodsReceiptNoteRepository.save(savedGoodsReceiptNote);
//     }

//     /**
//      * Deletes a given goods receipt note from the dba
//      */
//     @Override
//     public void delete(GoodsReceiptNote goodsReceiptNote) {
//         goodsReceiptNoteRepository.delete(goodsReceiptNote);
//     }

//     @Override
//     public int fulfill(GoodsReceiptNote goodsReceiptNote, GoodsReceiptNoteDto goodsReceiptNoteDto) {
//         goodsReceiptNote.setStatus(GrnStatus.RECEIVED);
//         PurchaseOrder purchaseOrder = goodsReceiptNote.getPurchaseOrder();
//         int purchaseOrderLines = purchaseOrder.getPurchaseOrderLines().size();
//         int receivedPurchaseOrderLines = 0;
//         // new goods receipt note for items not received or pending to receive in other shipments
//         GoodsReceiptNote newGoodsReceiptNote = GoodsReceiptNote.builder().purchaseOrder(purchaseOrder).status(GrnStatus.PENDING).date(LocalDate.now()).build(); 
//         List<GoodsReceiptNoteLine> pendingGoodsReceiptNoteLines = new ArrayList<>();
//         // Create a new good
//         for (int i=0; i<goodsReceiptNoteDto.getGoodsReceiptNoteLines().size(); i++) {
//             int qty = goodsReceiptNoteDto.getGoodsReceiptNoteLines().get(i).getQty();
//             Item item = goodsReceiptNote.getGoodsReceiptNoteLines().get(i).getItem();
//             Long warehouseSectionId = goodsReceiptNoteDto.getGoodsReceiptNoteLines().get(i).getWarehouseSectionId();
//             if (warehouseSectionId == null) {// Handling the possibility that front-end validations fails
//                 // creates a new goods receipt note for items not received
//                 GoodsReceiptNoteLine newGoodsReceiptNoteLine = GoodsReceiptNoteLine.builder().item(item).qty(qty).build();
//                 pendingGoodsReceiptNoteLines.add(newGoodsReceiptNoteLine);
//                 continue;
//             }
//             Optional<WarehouseSection> warehouseSection = warehouseSectionRepository.findById(warehouseSectionId);
//             Stock stock;
//             boolean damaged = (goodsReceiptNoteDto.getGoodsReceiptNoteLines().get(i).getDamaged() == 0) ? false : true;
//             // Place stocks on given warehosue or on damages section
//             if (damaged) {
//                 Optional<WarehouseSection> damagesWHSection = warehouseSectionRepository.findBySectionNumber(DAMAGE_WH_SECTION);
//                 stock = Stock.builder().qtyOnHand(qty).item(item).warehouseSection(damagesWHSection.get()).build();
//             } else {
//                 stock = Stock.builder().qtyOnHand(qty).item(item).warehouseSection(warehouseSection.get()).build();
//             }
             
//             stockRepository.save(stock);
//             receivedPurchaseOrderLines++;
//         }
//         if (receivedPurchaseOrderLines == 0) {
//             return NOT_FULFILLED;
//         }
//         else if (receivedPurchaseOrderLines < purchaseOrderLines) {
//             GoodsReceiptNote savedNewGoodsReceiptNote = goodsReceiptNoteRepository.save(newGoodsReceiptNote);
//             for (GoodsReceiptNoteLine newGoodsReceiptNoteLine : pendingGoodsReceiptNoteLines) {
//                 newGoodsReceiptNoteLine.setGoodsReceiptNote(savedNewGoodsReceiptNote);
//                 GoodsReceiptNoteLine savedNewGoodsReceiptNoteLine = goodsReceiptNoteLineRepository.save(newGoodsReceiptNoteLine);
//                 savedNewGoodsReceiptNote.getGoodsReceiptNoteLines().add(savedNewGoodsReceiptNoteLine);
//             }
//             goodsReceiptNoteRepository.save(savedNewGoodsReceiptNote);
//             return PARTIALLY_FULFILLED;
//         }
//         else {
//             goodsReceiptNoteRepository.save(goodsReceiptNote);
//             return FULFILLED;
//         }
//     }

//     @Override
//     public List<GoodsReceiptNote> findAllPending() {
//         return goodsReceiptNoteRepository.findAllPending();
//     }
    
// }
