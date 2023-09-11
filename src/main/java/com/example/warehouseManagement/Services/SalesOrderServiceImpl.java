package com.example.warehouseManagement.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Customer;
import com.example.warehouseManagement.Domains.LastThreeMonthsSales;
import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.SalesOrderLine;
import com.example.warehouseManagement.Domains.DTOs.PendingSalesOrderDto;
import com.example.warehouseManagement.Domains.DTOs.SalesOrderDto;
import com.example.warehouseManagement.Repositories.ItemPriceRepository;
import com.example.warehouseManagement.Repositories.PickingJobLineRepository;
import com.example.warehouseManagement.Repositories.PickingJobRepository;
import com.example.warehouseManagement.Repositories.SaleOrderLineRepository;
import com.example.warehouseManagement.Repositories.SalesOrderRepository;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SaleOrderLineRepository saleOrderLineRepository;
    private final ItemPriceRepository itemPriceRepository;
    private final PickingJobRepository pickingJobRepository;
    private final PickingJobLineRepository pickingJobLineRepository;

    public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository,
            SaleOrderLineRepository saleOrderLineRepository,
            ItemPriceRepository itemPriceRepository,
            PickingJobRepository pickingJobRepository,
            PickingJobLineRepository pickingJobLineRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.saleOrderLineRepository = saleOrderLineRepository;
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
    public SalesOrder save(SalesOrder salesOrder) {
        //TODO: TEST WITHOUT PERSISTING CHILDS ENTITIES USING REPO SINCE PARENT CASCADE TYPE IS ALL
        SalesOrder so = salesOrderRepository.save(salesOrder);
        // Create a PickingJob since orders has to be fulfilled by wh staff
        PickingJob pickingJob = PickingJob.builder().salesOrder(so).build();
        PickingJob savedPickingJob = pickingJobRepository.save(pickingJob);
        for (SalesOrderLine sol : so.getSaleOrderLines()) {
            sol.setSalesOrder(salesOrder);
            sol.setItemPrice(itemPriceRepository.findCurrentItemPriceByItemId(sol.getItem().getId()));
            // Creating a new picking job line
            PickingJobLine pickingJobLine = PickingJobLine.builder().item(sol.getItem()).qtyToPick(sol.getQty()).pickingJob(savedPickingJob).build();
            // Adding picking job line to saved picked job
            savedPickingJob.getPickingJobLines().add(pickingJobLine);
        }
        saleOrderLineRepository.saveAll(so.getSaleOrderLines());
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
     * Returns a list of all the pending to ship sales order by a given year and month
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
        return LastThreeMonthsSales.builder().lastThreeMonthsSales(salesOrderRepository.findLastThreeMonthsSales()).build(); 
    }
    
}
