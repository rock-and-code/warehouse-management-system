package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderDto;
import com.example.warehouseManagement.Repositories.ItemCostRepository;
import com.example.warehouseManagement.Repositories.PurchaseOrderLineRepository;
import com.example.warehouseManagement.Repositories.PurchaseOrderRepository;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final ItemCostRepository itemCostRepository;
    
    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderLineRepository purchaseOrderLineRepository,
            ItemCostRepository itemCostRepository) {
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.itemCostRepository = itemCostRepository;
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
     * Persists a Purchase order in the dba
     */
    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        PurchaseOrder po = purchaseOrderRepository.save(purchaseOrder);
        for (PurchaseOrderLine pol : po.getPurchaseOrderLines()) {
            pol.setPurchaseOrder(purchaseOrder);
            pol.setItemCost(itemCostRepository.findCurrentItemCostByItemId(pol.getItem().getId()));
        }
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
    
}
