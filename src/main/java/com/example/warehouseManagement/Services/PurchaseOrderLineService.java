package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.PurchaseOrderLine;

public interface PurchaseOrderLineService {
     /**
     * Returns a list of puchase order lines by puchase order
     * @param purchaseOrder
     * @return
     */
    public List<PurchaseOrderLine> findByPurchaseOrder(PurchaseOrder purchaseOrder);
    /**
     * Saves a new purchase order line in the dba
     * @param purchaseOrderLine
     * @return
     */
    public PurchaseOrderLine save(PurchaseOrderLine purchaseOrderLine);
    /**
     * Deletes a purchase order line from the dba
     * @param saleOrderLine
     */
    public void delete(PurchaseOrderLine purchaseOrderLine);
    /**
     * Deletes all the purchase order lines by a purhcase order
     * @param purchaseOrder
     */
    public void deleteAllByPurchaseOrder(PurchaseOrder purchaseOrder);
}
