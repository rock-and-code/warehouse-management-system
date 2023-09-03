package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;
import com.example.warehouseManagement.Repositories.PurchaseOrderLineRepository;

public class PurchaseOrderLineServiceImpl implements PurchaseOrderLineService {
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    

    public PurchaseOrderLineServiceImpl(PurchaseOrderLineRepository purchaseOrderLineRepository) {
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
    }

    @Override
    public List<PurchaseOrderLine> findByPurchaseOrder(PurchaseOrder purchaseOrder) {
        return purchaseOrderLineRepository.findByPurchaseOrder(purchaseOrder);
    }

    @Override
    public PurchaseOrderLine save(PurchaseOrderLine purchaseOrderLine) {
        return purchaseOrderLineRepository.save(purchaseOrderLine);
    }

    @Override
    public void delete(PurchaseOrderLine purchaseOrderLine) {
        purchaseOrderLineRepository.delete(purchaseOrderLine);
    }

    @Override
    public void deleteAllByPurchaseOrder(PurchaseOrder purchaseOrder) {
        purchaseOrderLineRepository.deleteAllByPurchaseOrder(purchaseOrder);
    }
    
}
