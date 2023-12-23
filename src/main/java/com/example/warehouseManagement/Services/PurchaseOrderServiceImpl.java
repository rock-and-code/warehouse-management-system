package com.example.warehouseManagement.Services;

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
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderDto;
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

}
