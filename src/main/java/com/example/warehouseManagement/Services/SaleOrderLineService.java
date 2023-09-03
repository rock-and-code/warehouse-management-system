package com.example.warehouseManagement.Services;

import java.util.List;

import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrderLine;

public interface SaleOrderLineService {
    /**
     * Returns a list of sales order lines by a given sales order
     * @param salesOrder
     * @return
     */
    public List<SalesOrderLine> findBySaleOrder(SalesOrder salesOrder);
    /**
     * Saves a new sale order line in the dba
     * @param saleOrderLine
     * @return
     */
    public SalesOrderLine save(SalesOrderLine saleOrderLine);
    /**
     * Deletes a sales order line from the dba
     * @param saleOrderLine
     */
    public void delete(SalesOrderLine saleOrderLine);
    /**
     * Deletes all the sales order lines of a given sales order id
     * @param salesOrder
     */
    public void deleteAllBySalesOrder(SalesOrder salesOrder);
}
