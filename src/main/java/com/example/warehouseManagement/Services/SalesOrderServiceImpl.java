package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.Item;
import com.example.warehouseManagement.Domains.LastThreeMonthsSales;
import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrder.SoStatus;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Domains.DTOs.PendingSalesOrderDto;
import com.example.warehouseManagement.Domains.DTOs.SalesOrderDto;
import com.example.warehouseManagement.Domains.Exceptions.SalesOrderNotFoundException;
import com.example.warehouseManagement.Domains.Exceptions.ShippedOrderModificationException;
import com.example.warehouseManagement.Repositories.ItemPriceRepository;
import com.example.warehouseManagement.Repositories.ItemRepository;
import com.example.warehouseManagement.Repositories.PickingJobLineRepository;
import com.example.warehouseManagement.Repositories.PickingJobRepository;
import com.example.warehouseManagement.Repositories.SaleOrderLineRepository;
import com.example.warehouseManagement.Repositories.SalesOrderRepository;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SaleOrderLineRepository salesOrderLineRepository;
    private final ItemRepository itemRepository;
    private final ItemPriceRepository itemPriceRepository;
    private final PickingJobRepository pickingJobRepository;
    private final PickingJobLineRepository pickingJobLineRepository;

    public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository,
            SaleOrderLineRepository salesOrderLineRepository,
            ItemRepository itemRepository,
            ItemPriceRepository itemPriceRepository,
            PickingJobRepository pickingJobRepository,
            PickingJobLineRepository pickingJobLineRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.itemRepository = itemRepository;
        this.itemPriceRepository = itemPriceRepository;
        this.pickingJobRepository = pickingJobRepository;
        this.pickingJobLineRepository = pickingJobLineRepository;
    }

    @Override
    public Optional<SalesOrder> findById(Long id) {
        return salesOrderRepository.findById(id);
    }

    @Override
    public List<SalesOrder> findByCustomer(Customer customer) {
        return salesOrderRepository.findByCustomer(customer);
    }

    @Override
    public Iterable<SalesOrder> findAll() {
        return salesOrderRepository.findAll();
    }

    @Override
    public List<SalesOrderDto> findAllByCustomer(Long customerId) {
        return salesOrderRepository.findAllByCustomer(customerId);
    }

    @Override
    public SalesOrder updateById(Long id, SalesOrder salesOrder)
            throws SalesOrderNotFoundException, ShippedOrderModificationException {
        if (salesOrderRepository.findById(id).isEmpty()) {
            throw new SalesOrderNotFoundException();
        } else {
            SalesOrder existing = salesOrderRepository.findById(id).get();
            if (existing.getStatus() == SoStatus.SHIPPED) {
                throw new ShippedOrderModificationException();
            }
            // Check if backorders were delete, if so mark order as shipped
            if (existing.getBackorders().size() == 0 && existing.getStatus() == SoStatus.PARTIALLY_SHIPPED) {
                existing.setStatus(SoStatus.SHIPPED);
            }
            // Business Rules -> Only allow sales order modification on customer (for
            // clients with differents corporations or that changed corporation)
            // And changes in sales order lines
            existing.setCustomer(salesOrder.getCustomer());

            for (int i=0; i<existing.getSaleOrderLines().size(); i++) {
                // Checks if sales order line item was modified
                Long existingSolItemId = existing.getSaleOrderLines().get(i).getItem().getId();
                Long modifiedSolItemId = salesOrder.getSaleOrderLines().get(i).getItem().getId();
                int existingSolQty = existing.getSaleOrderLines().get(i).getQty();
                int modifiedSolQty = salesOrder.getSaleOrderLines().get(i).getQty();

                if (existingSolItemId != modifiedSolItemId) {
                    Item modifiedItem = itemRepository.findById(modifiedSolItemId).get();
                    existing.getSaleOrderLines().get(i).setItem(modifiedItem);
                }
                if (existingSolQty != modifiedSolQty) {
                    existing.getSaleOrderLines().get(i).setQty(modifiedSolQty);
                } 
            }
            // Checks if a new sales order line was added to existing sales order
            int existingSalesOrderLines = existing.getSaleOrderLines().size();
            int modifiedSalesOrderLines = salesOrder.getSaleOrderLines().size();
            if (existingSalesOrderLines < modifiedSalesOrderLines) {
                for (int i=existingSalesOrderLines; i<modifiedSalesOrderLines; i++) {
                    Long newItemId = salesOrder.getSaleOrderLines().get(i).getItem().getId();
                    int qty = salesOrder.getSaleOrderLines().get(i).getQty();
                    Item modifiedItem = itemRepository.findById(newItemId).get();
                    SalesOrderLine newSalesOrderLine = SalesOrderLine.builder().item(modifiedItem)
                        .itemPrice(itemPriceRepository.findCurrentItemPriceByItemId(newItemId))
                        .qty(qty).salesOrder(existing).build();
                    salesOrderLineRepository.save(newSalesOrderLine);
                }
            }

            return salesOrderRepository.save(existing);
        }
    }

    @Override
    public SalesOrder save(SalesOrder salesOrder) {
        // TODO: TEST WITHOUT PERSISTING CHILDS ENTITIES USING REPO SINCE PARENT CASCADE
        // TYPE IS ALL
        SalesOrder so = salesOrderRepository.save(salesOrder);
        // Create a PickingJob since orders has to be fulfilled by wh staff
        PickingJob pickingJob = PickingJob.builder().salesOrder(so).build();
        PickingJob savedPickingJob = pickingJobRepository.save(pickingJob);
        for (SalesOrderLine sol : so.getSaleOrderLines()) {
            sol.setSalesOrder(salesOrder);
            sol.setItemPrice(itemPriceRepository.findCurrentItemPriceByItemId(sol.getItem().getId()));
            // Creating a new picking job line
            PickingJobLine pickingJobLine = PickingJobLine.builder().item(sol.getItem()).qtyToPick(sol.getQty())
                    .pickingJob(savedPickingJob).build();
            // Adding picking job line to saved picked job
            savedPickingJob.getPickingJobLines().add(pickingJobLine);
        }
        salesOrderLineRepository.saveAll(so.getSaleOrderLines());
        pickingJobLineRepository.saveAll(savedPickingJob.getPickingJobLines());
        pickingJobRepository.save(savedPickingJob);
        return salesOrderRepository.save(so);
    }

    @Override
    public void delete(SalesOrder salesOrder) {
        salesOrderRepository.delete(salesOrder);
    }

    @Override
    public List<SalesOrder> findAllPendingToShipOrders() {
        return salesOrderRepository.findAllPendingToShipOrders();
    }

    /**
     * Returns a list of all the pending to ship sales order by a given year and
     * month
     * 
     * @param year
     * @param month
     * @return
     */
    public List<PendingSalesOrderDto> findAllPendingToShipOrdersByYearAndMonth(int year, String month) {
        return salesOrderRepository.findAllPendingToShipOrdersByYearAndMonth(year, month);
    }

    @Override
    public List<SalesOrderDto> findAllSalesOrder() {
        return salesOrderRepository.findAllSalesOrder();
    }

    @Override
    public LastThreeMonthsSales findLastThreeMonthsSales() {
        return LastThreeMonthsSales.builder().lastThreeMonthsSales(salesOrderRepository.findLastThreeMonthsSales())
                .build();
    }

    @Override
    public List<SalesOrderDto> findAllPendingOrders() {
        return salesOrderRepository.findAllPendingSalesOrder();
    }

}
