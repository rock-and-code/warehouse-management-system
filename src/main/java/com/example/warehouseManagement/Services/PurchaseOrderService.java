package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.warehouseManagement.Domains.PurchaseOrder;
import com.example.warehouseManagement.Domains.DTOs.AgingPoDto;
import com.example.warehouseManagement.Domains.DTOs.OpenOrdersKpiDto;
import com.example.warehouseManagement.Domains.DTOs.PoStatusBucketDto;
import com.example.warehouseManagement.Domains.DTOs.PurchaseOrderDto;
import com.example.warehouseManagement.Domains.DTOs.VendorSpendDto;
import com.example.warehouseManagement.Domains.Exceptions.PurchaseOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ReceivedOrderModificationException;

public interface PurchaseOrderService {
    /**
     * Returns a list of all the purchases order persisted in the DBA
     * @return
     */
    public Iterable<PurchaseOrder> findAll();
    /**
     * Paginated + sortable variant used by the list page.
     */
    public Page<PurchaseOrder> findAll(Pageable pageable);
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
     * Returns a list of all the purchase orders persisted in the dba
     * @return
     */
    public List<PurchaseOrderDto> findAllPurchaseOrder();
    /**
     * Returns a list of all the pending purchase orders persisted in the dba
     * @return
     */
    public List<PurchaseOrderDto> findAllPendingPurchaseOrder();

    /** Dashboard KPI: count + value of open purchase orders. */
    OpenOrdersKpiDto findOpenPurchaseOrdersKpi();

    /** Top 5 vendors by year-to-date spend, used by the dashboard bar chart. */
    List<VendorSpendDto> findTopVendorsBySpendYtd();

    /** PO count grouped by status (0/1/2) — feeds the dashboard doughnut. */
    List<PoStatusBucketDto> findPoStatusBuckets();

    /** Open POs older than 30 days, oldest first — feeds the aging table. */
    List<AgingPoDto> findAgingPurchaseOrders();

}
