package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJob.PjStatus;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.DTOs.PickingJobDto;
import com.example.warehouseManagement.Repositories.BackorderRepository;
import com.example.warehouseManagement.Repositories.PickingJobLineRepository;
import com.example.warehouseManagement.Repositories.PickingJobRepository;
import com.example.warehouseManagement.Repositories.SalesOrderRepository;
import com.example.warehouseManagement.Repositories.StockRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

@Service
public class PickingJobServiceImpl implements PickingJobService {
    private final PickingJobRepository pickingJobRepository;
    private final PickingJobLineRepository pickingJobLineRepository;
    private final WarehouseSectionRepository warehouseSectionRepository;
    private final StockRepository stockRepository;
    private final BackorderRepository backorderRepository;
    private final SalesOrderRepository salesOrderRepository;

    public PickingJobServiceImpl(PickingJobRepository pickingJobRepository,
            PickingJobLineRepository pickingJobLineRepository,
            WarehouseSectionRepository warehouseSectionRepository,
            StockRepository stockRepository,
            BackorderRepository backorderRepository,
            SalesOrderRepository salesOrderRepository) {
        this.pickingJobRepository = pickingJobRepository;
        this.pickingJobLineRepository = pickingJobLineRepository;
        this.warehouseSectionRepository = warehouseSectionRepository;
        this.stockRepository = stockRepository;
        this.backorderRepository = backorderRepository;
        this.salesOrderRepository = salesOrderRepository;
    }

    @Override
    public Iterable<PickingJob> findAll() {
        return pickingJobRepository.findAll();
    }

    @Override
    public Optional<PickingJob> findById(Long id) {
        return pickingJobRepository.findById(id);
    }

    @Override
    public PickingJob save(PickingJob pickingJob) {
        // return pickingJobRepository.save(pickingJob);
        PickingJob savedPickingJob = pickingJobRepository.save(pickingJob);
        for (PickingJobLine pickingJobLine : savedPickingJob.getPickingJobLines())
            pickingJobLine.setPickingJob(savedPickingJob);
        pickingJobLineRepository.saveAll(savedPickingJob.getPickingJobLines());
        return pickingJobRepository.save(savedPickingJob);
    }

    @Override
    public void delete(PickingJob pickingJob) {
        pickingJobRepository.delete(pickingJob);
    }

    @Override
    public List<PickingJob> findAllPending() {
        return pickingJobRepository.findAllPending();
    }

    /**
     * Simulates the picking process to fulfill a sales order
     * Validates that qty picked on supplied picking job dto is available at the selected bin
     * if not, it will create another picking job line for the backorder 
     */
    @Override
    public PickingJob fulfill(PickingJob pickingJob, PickingJobDto pickingJobDto) {
        //Collections to collect fulfilled and not fulfilled picking job lines
        List<PickingJobLine> pickingJobDtoLinesForBackOrders = new ArrayList<>();
        List<PickingJobLine> savedPickingJobLines = new ArrayList<>();

        //Iterating over the picking job lines to fullfill it
        for (int i = 0; i < pickingJob.getPickingJobLines().size(); i++) {
            Long warehouseSectionId = pickingJobDto.getPickingJobDtoLines().get(i).getWareHouseSectionId();
            Long itemId = pickingJob.getPickingJobLines().get(i).getItem().getId();
            int qtyPicked = pickingJobDto.getPickingJobDtoLines().get(i).getQtyPicked();
            PickingJobLine pickingJobLine = pickingJob.getPickingJobLines().get(i);
            Stock stock = stockRepository.findByWarehouseSectionAndItemId(warehouseSectionId, itemId);

            //Checks whether current picking job line was picked or not due to out-of-stocks
            if (stock == null || warehouseSectionId == 0L || qtyPicked == 0) { 
                //Out-of-stock thus gather picking job lines un picked to create another picking job for backorders
                pickingJobDtoLinesForBackOrders.add(pickingJobLine);
                pickingJob.getPickingJobLines().remove(i);
                pickingJobLineRepository.delete(pickingJobLine); // since it is a backorder
            } else {
                // Checks if there is sufficient qty on hand at the selected warehouse bin to fulfill the picking job line
                if (stock.getQtyOnHand() >= qtyPicked) {
                    pickingJobLine.setQtyPicked(pickingJobDto.getPickingJobDtoLines().get(i).getQtyPicked());
                } else {
                    pickingJobLine.setQtyPicked(stock.getQtyOnHand());
                }
                pickingJobLine.setWarehouseSection(warehouseSectionRepository
                        .findById(pickingJobDto.getPickingJobDtoLines().get(i).getWareHouseSectionId()).get());
                savedPickingJobLines.add(pickingJobLineRepository.save(pickingJobLine));
            }

        }

        //Checks if there are backorders to create a new picking job 
        if (pickingJobDtoLinesForBackOrders.size() > 0) {
            PickingJob pickingJobForBackOrders = PickingJob.builder().salesOrder(pickingJob.getSalesOrder()).build();
            PickingJob savedPickingJobForBackOrders = pickingJobRepository.save(pickingJobForBackOrders);
            for (PickingJobLine pickingJobLine : pickingJobDtoLinesForBackOrders) {
                PickingJobLine pickingJobLineForBackOrder = PickingJobLine.builder()
                        .pickingJob(savedPickingJobForBackOrders).item(pickingJobLine.getItem())
                        .qtyToPick(pickingJobLine.getQtyToPick()).qtyPicked(0).build();
                PickingJobLine savedPickingJobLineForBackOrder = pickingJobLineRepository
                        .save(pickingJobLineForBackOrder);
                pickingJobForBackOrders.getPickingJobLines().add(savedPickingJobLineForBackOrder);

                SalesOrder salesOrder = pickingJob.getSalesOrder();
                //Creating backorder
                Backorder backorder = Backorder.builder().qty(pickingJobLine.getQtyToPick()).item(pickingJobLine.getItem())
                                .salesOrder(salesOrder).build();
                //Persisting backorder in database
                Backorder savedBackorder = backorderRepository.save(backorder);
                //Adding persisted backorder to sales order entity
                salesOrder.getBackorders().add(savedBackorder);
                //updating persisted sales order
                salesOrderRepository.save(salesOrder);
            }
            pickingJobRepository.save(pickingJobForBackOrders);
        }
        pickingJob.setStatus(PjStatus.FULFILLED);
        pickingJob.setPickingJobLines(savedPickingJobLines);
        return pickingJobRepository.save(pickingJob);
    }

}
