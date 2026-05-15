package com.example.warehouseManagement.Services;

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
import com.example.warehouseManagement.Domains.DTOs.AdvancedPoSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.TextMode;
import com.example.warehouseManagement.Domains.DTOs.AgingPoDto;
import com.example.warehouseManagement.Domains.DTOs.OpenOrdersKpiDto;
import com.example.warehouseManagement.Domains.DTOs.PoStatusBucketDto;
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderDto;
import com.example.warehouseManagement.Domains.DTOs.VendorSpendDto;

import jakarta.persistence.criteria.Predicate;
import com.example.warehouseManagement.Domains.Exceptions.PurchaseOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ReceivedOrderModificationException;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteLineRepository;
import com.example.warehouseManagement.Repositories.GoodsReceiptNoteRepository;
import com.example.warehouseManagement.Repositories.ItemCostRepository;
import com.example.warehouseManagement.Repositories.ItemRepository;
import com.example.warehouseManagement.Repositories.PurchaseOrderLineRepository;
import com.example.warehouseManagement.Repositories.PurchaseOrderRepository;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final ItemRepository itemRepository;
    private final ItemCostRepository itemCostRepository;
    private final GoodsReceiptNoteRepository goodsReceiptNoteRepository;
    private final GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderLineRepository purchaseOrderLineRepository,
            ItemRepository itemRepository,
            ItemCostRepository itemCostRepository,
            GoodsReceiptNoteRepository goodsReceiptNoteRepository,
            GoodsReceiptNoteLineRepository goodsReceiptNoteLineRepository) {
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.itemRepository = itemRepository;
        this.itemCostRepository = itemCostRepository;
        this.goodsReceiptNoteRepository = goodsReceiptNoteRepository;
        this.goodsReceiptNoteLineRepository = goodsReceiptNoteLineRepository;
    }

    /**
     * Returns a list of all the purchase orders persisted in the DBA
     */
    @Override
    public Iterable<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Page<PurchaseOrder> findAll(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable);
    }

    @Override
    public Page<PurchaseOrder> findPendingPage(Pageable pageable) {
        return purchaseOrderRepository.findByStatus(PoStatus.IN_TRANSIT, pageable);
    }

    @Override
    public Page<PurchaseOrder> findAdvanced(AdvancedPoSearchCriteria criteria, Pageable pageable) {
        return purchaseOrderRepository.findAll(buildSpec(criteria), pageable);
    }

    /**
     * AND-combine every populated filter. Empty / null fields drop out.
     */
    private static Specification<PurchaseOrder> buildSpec(AdvancedPoSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (c.getId() != null && !c.getId().isBlank()) {
                String idStr = c.getId().trim();
                TextMode mode = c.getIdMode() == null ? TextMode.STARTS_WITH : c.getIdMode();
                if (mode == TextMode.EQUALS) {
                    // Exact equality — parse as Long so we still hit the indexed id column.
                    try {
                        ps.add(cb.equal(root.get("id"), Long.parseLong(idStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction()); // un-parseable id → no match
                    }
                } else {
                    // Partial match — cast id to text and LIKE against it.
                    var idText = root.<Long>get("id").as(String.class);
                    String pattern = (mode == TextMode.STARTS_WITH)
                            ? idStr + "%"
                            : "%" + idStr + "%";
                    ps.add(cb.like(idText, pattern));
                }
            }

            if (c.getVendor() != null && !c.getVendor().isBlank()) {
                String v = c.getVendor().trim().toLowerCase();
                var vendorName = cb.lower(root.get("vendor").get("name"));
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

    /**
     * Returns a purchase order that matches the given id
     */
    @Override
    public Optional<PurchaseOrder> findById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    /**
     * Returns a purchase order that matches the given purchase order number
     */
    @Override
    public Optional<PurchaseOrder> findByPurchaseOrderNumber(int purchaseOrderNumber) {
        return purchaseOrderRepository.findByPurchaseOrderNumber(purchaseOrderNumber);
    }

    /**
     * Return a list of all the purchase orders by a given vendor
     */
    @Override
    public List<PurchaseOrderDto> findAllByVendor(Long vendorId) {
        return purchaseOrderRepository.findAllByVendor(vendorId);
    }
    /**
     * Update an existing purchase order in the db by its id
     * @param id
     * @param purchaseOrder
     * @return
     */
    @Override
    @Transactional
    public PurchaseOrder updateById(Long id, PurchaseOrder purchaseOrder) throws PurchaseOrderNotFoundException, ReceivedOrderModificationException {
        if (purchaseOrderRepository.findById(id).isEmpty()) {
            throw new PurchaseOrderNotFoundException();
        } else {
            PurchaseOrder existing = purchaseOrderRepository.findById(id).get();
            if (existing.getStatus() == PoStatus.RECEIVED) {
                throw new ReceivedOrderModificationException();
            }

            for (int i=0; i<existing.getPurchaseOrderLines().size(); i++) {
                // Checks if sales order line item was modified
                Long existingSolItemId = existing.getPurchaseOrderLines().get(i).getItem().getId();
                Long modifiedSolItemId = purchaseOrder.getPurchaseOrderLines().get(i).getItem().getId();
                int existingSolQty = existing.getPurchaseOrderLines().get(i).getQty();
                int modifiedSolQty = purchaseOrder.getPurchaseOrderLines().get(i).getQty();

                if (existingSolItemId != modifiedSolItemId) {
                    Item modifiedItem = itemRepository.findById(modifiedSolItemId).get();
                    existing.getPurchaseOrderLines().get(i).setItem(modifiedItem);
                }
                if (existingSolQty != modifiedSolQty) {
                    existing.getPurchaseOrderLines().get(i).setQty(modifiedSolQty);
                } 
            }
            // Checks if a new sales order line was added to existing sales order
            int existingpurchaseOrderLines = existing.getPurchaseOrderLines().size();
            int modifiedpurchaseOrderLines = purchaseOrder.getPurchaseOrderLines().size();
            if (existingpurchaseOrderLines < modifiedpurchaseOrderLines) {
                for (int i=existingpurchaseOrderLines; i<modifiedpurchaseOrderLines; i++) {
                    Long newItemId = purchaseOrder.getPurchaseOrderLines().get(i).getItem().getId();
                    int qty = purchaseOrder.getPurchaseOrderLines().get(i).getQty();
                    Item modifiedItem = itemRepository.findById(newItemId).get();
                    PurchaseOrderLine newpurchaseOrderLine = PurchaseOrderLine.builder().item(modifiedItem)
                        .itemCost(itemCostRepository.findCurrentItemCostByItemId(newItemId))
                        .qty(qty).purchaseOrder(existing).build();
                    purchaseOrderLineRepository.save(newpurchaseOrderLine);
                }
            }

            return purchaseOrderRepository.save(existing);
        }
    }

    /**
     * Persists a Purchase order in the dba
     */
    @Override
    @Transactional
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        PurchaseOrder po = purchaseOrderRepository.save(purchaseOrder);
        // Adds goods receipt note
        GoodsReceiptNote goodsReceiptNote = GoodsReceiptNote.builder().date(po.getDate())
                .purchaseOrder(po).date(po.getDate()).status(GrnStatus.PENDING).build();
        GoodsReceiptNote savedGoodsReceiptNote = goodsReceiptNoteRepository.save(goodsReceiptNote);
        for (PurchaseOrderLine pol : po.getPurchaseOrderLines()) {
            pol.setPurchaseOrder(purchaseOrder);
            pol.setItemCost(itemCostRepository.findCurrentItemCostByItemId(pol.getItem().getId()));
            // Adding Goods receipt lines
            GoodsReceiptNoteLine goodsReceiptNoteLine = GoodsReceiptNoteLine.builder()
                    .goodsReceiptNote(savedGoodsReceiptNote)
                    .qty(pol.getQty()).item(pol.getItem()).build();
            GoodsReceiptNoteLine savedGoodsReceiptNoteLine = goodsReceiptNoteLineRepository
                    .save(goodsReceiptNoteLine);
            savedGoodsReceiptNote.getGoodsReceiptNoteLines().add(savedGoodsReceiptNoteLine);
            po.getGoodsReceiptNotes().add(savedGoodsReceiptNote);
        }
        goodsReceiptNoteRepository.save(savedGoodsReceiptNote);
        purchaseOrderLineRepository.saveAll(po.getPurchaseOrderLines());
        return purchaseOrderRepository.save(po);
    }

    /**
     * Delete a persisted purchase order from the DBA
     */
    @Override
    @Transactional
    public void delete(PurchaseOrder purchaseOrder) {
        purchaseOrderRepository.delete(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderDto> findAllPurchaseOrder() {
        return purchaseOrderRepository.findAllPurchaseOrder();
    }

    @Override
    public List<PurchaseOrderDto> findAllPendingPurchaseOrder() {
        return purchaseOrderRepository.findAllPendingPurchaseOrder();
    }

    @Override
    public OpenOrdersKpiDto findOpenPurchaseOrdersKpi() {
        return purchaseOrderRepository.findOpenPurchaseOrdersKpi();
    }

    @Override
    public List<VendorSpendDto> findTopVendorsBySpendYtd() {
        return purchaseOrderRepository.findTopVendorsBySpendYtd();
    }

    @Override
    public List<PoStatusBucketDto> findPoStatusBuckets() {
        return purchaseOrderRepository.findPoStatusBuckets();
    }

    @Override
    public List<AgingPoDto> findAgingPurchaseOrders() {
        return purchaseOrderRepository.findAgingPurchaseOrders();
    }

}
