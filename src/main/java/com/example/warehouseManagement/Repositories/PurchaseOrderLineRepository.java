package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;

public interface PurchaseOrderLineRepository extends CrudRepository<PurchaseOrderLine, Long>{
    /**
     * Returns a list of puchase order lines by puchase order
     * @param purchaseOrder
     * @return
     */
    public List<PurchaseOrderLine> findByPurchaseOrder(PurchaseOrder purchaseOrder);
    /**
     * Deletes all the purchase order lines by a purhcase order
     * @param purchaseOrder
     */
    public void deleteAllByPurchaseOrder(PurchaseOrder purchaseOrder);
}
