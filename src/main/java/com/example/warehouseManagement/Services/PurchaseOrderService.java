package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderDto;
import com.example.warehouseManagement.Domains.Exceptions.PurchaseOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ReceivedOrderModificationException;

public interface PurchaseOrderService {
    /**
     * Returns a list of all the purchases order persisted in the DBA
     * @return
     */
    public Iterable<PurchaseOrder> findAll();
    /**
     * Returns a purchase order by a given id
     * @param id
     * @return
     */
    public Optional<PurchaseOrder> findById(Long id);
    /**
     * Returns a purchase order by a given purchase order number
     * @param purchaseOrderNumber
     * @return
     */
    public Optional<PurchaseOrder> findByPurchaseOrderNumber(int purchaseOrderNumber);
    /**
     * Returns a list of all the purchase orders by a given vendor
     * @param vendor
     * @return
     */
    public List<PurchaseOrderDto> findAllByVendor(Long vendorId);
    /**
     * Update an existing purchase order in the db by its id
     * @param id
     * @param purchaseOrder
     * @return
     */
    public PurchaseOrder updateById(Long id, PurchaseOrder purchaseOrder) throws PurchaseOrderNotFoundException, ReceivedOrderModificationException;
    /**
     * Persist a given purchase order in the DBA
     * @param purchaseOrder
     * @return
     */
    public PurchaseOrder save(PurchaseOrder purchaseOrder);
    /**
     * Deletes a purchase order from the DBA
     * @param purchaseOrder
     */
    public void delete(PurchaseOrder purchaseOrder);
    /**
     * Returns a list of all the sales orders persisted in the dba
     * @return
     */
    public List<PurchaseOrderDto> findAllPurchaseOrder();

}
