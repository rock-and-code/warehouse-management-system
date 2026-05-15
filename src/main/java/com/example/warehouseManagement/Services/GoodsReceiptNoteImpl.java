package com.example.warehouseManagement.Services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.warehouseManagement.Domains.GoodsReceiptNote;
import com.example.warehouseManagement.Domains.GoodsReceiptNote.GrnStatus;
import com.example.warehouseManagement.Domains.GoodsReceiptNoteLine;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrder.PoStatus;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.WarehouseSection;
import com.example.warehouseManagement.Domains.DTOs.AdvancedGrnSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteDto;
import com.example.warehouseManagement.Domains.DTOs.GoodsReceiptNoteLineDto;
import com.example.warehouseManagement.Domains.DTOs.TextMode;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteLineRepository;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteRepository;
import com.example.warehouseManagement.Repositories.StockRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

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
    @Transactional
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
            // Records notes made on grn line
            goodsReceiptNoteLine.setNotes(notes);
            // Records the qty received in the goods receipt note persisted in db
            goodsReceiptNoteLine.setQty(qty);
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
    public Page<GoodsReceiptNote> findAdvanced(AdvancedGrnSearchCriteria criteria, Pageable pageable) {
        return goodsReceiptNoteRepository.findAll(buildSpec(criteria), pageable);
    }

    private static Specification<GoodsReceiptNote> buildSpec(AdvancedGrnSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (c.getId() != null && !c.getId().isBlank()) {
                String idStr = c.getId().trim();
                TextMode mode = c.getIdMode() == null ? TextMode.STARTS_WITH : c.getIdMode();
                if (mode == TextMode.EQUALS) {
                    try {
                        ps.add(cb.equal(root.get("id"), Long.parseLong(idStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction());
                    }
                } else {
                    Expression<String> idText = root.<Long>get("id").as(String.class);
                    String pattern = (mode == TextMode.STARTS_WITH) ? idStr + "%" : "%" + idStr + "%";
                    ps.add(cb.like(idText, pattern));
                }
            }

            if (c.getPurchaseOrderId() != null && !c.getPurchaseOrderId().isBlank()) {
                String poStr = c.getPurchaseOrderId().trim();
                TextMode mode = c.getPurchaseOrderIdMode() == null ? TextMode.STARTS_WITH : c.getPurchaseOrderIdMode();
                if (mode == TextMode.EQUALS) {
                    try {
                        ps.add(cb.equal(root.get("purchaseOrder").get("id"), Long.parseLong(poStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction());
                    }
                } else {
                    Expression<String> poText = root.get("purchaseOrder").<Long>get("id").as(String.class);
                    String pattern = (mode == TextMode.STARTS_WITH) ? poStr + "%" : "%" + poStr + "%";
                    ps.add(cb.like(poText, pattern));
                }
            }

            if (c.getVendor() != null && !c.getVendor().isBlank()) {
                String v = c.getVendor().trim().toLowerCase();
                Expression<String> vendorName = cb.lower(root.get("purchaseOrder").get("vendor").get("name"));
                switch (c.getVendorMode() == null ? TextMode.CONTAINS : c.getVendorMode()) {
                    case EQUALS      -> ps.add(cb.equal(vendorName, v));
                    case STARTS_WITH -> ps.add(cb.like(vendorName, v + "%"));
                    case CONTAINS    -> ps.add(cb.like(vendorName, "%" + v + "%"));
                }
            }

            if (c.getDateFrom() != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("date"), c.getDateFrom()));
            }
            if (c.getDateTo() != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("date"), c.getDateTo()));
            }
            if (c.getStatus() != null) {
                ps.add(cb.equal(root.get("status"), c.getStatus()));
            }

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(Predicate[]::new));
        };
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

    @Override
    public long countPending() {
        return goodsReceiptNoteRepository.countByStatus(GrnStatus.PENDING);
    }

}
