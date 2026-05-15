package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.warehouseManagement.Domains.Backorder;
import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJob.PjStatus;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Domains.SalesOrder;
import com.example.warehouseManagement.Domains.Stock;
import com.example.warehouseManagement.Domains.DTOs.AdvancedPickingJobSearchCriteria;
import com.example.warehouseManagement.Domains.DTOs.PickingJobDto;
import com.example.warehouseManagement.Domains.DTOs.TextMode;
import com.example.warehouseManagement.Repositories.BackorderRepository;
import com.example.warehouseManagement.Repositories.PickingJobLineRepository;
import com.example.warehouseManagement.Repositories.PickingJobRepository;
import com.example.warehouseManagement.Repositories.SalesOrderRepository;
import com.example.warehouseManagement.Repositories.StockRepository;
import com.example.warehouseManagement.Repositories.WarehouseSectionRepository;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

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
    public Page<PickingJob> findAdvanced(AdvancedPickingJobSearchCriteria criteria, Pageable pageable) {
        return pickingJobRepository.findAll(buildSpec(criteria), pageable);
    }

    private static Specification<PickingJob> buildSpec(AdvancedPickingJobSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (c.getId() != null && !c.getId().isBlank()) {
                String idStr = c.getId().trim();
                TextMode mode = c.getIdMode() == null ? TextMode.STARTS_WITH : c.getIdMode();
                if (mode == TextMode.EQUALS) {
                    try {
                        ps.add(cb.equal(root.get("id"), Long.parseLong(idStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction());
                    }
                } else {
                    Expression<String> idText = root.<Long>get("id").as(String.class);
                    String pattern = (mode == TextMode.STARTS_WITH) ? idStr + "%" : "%" + idStr + "%";
                    ps.add(cb.like(idText, pattern));
                }
            }

            if (c.getSalesOrderId() != null && !c.getSalesOrderId().isBlank()) {
                String soStr = c.getSalesOrderId().trim();
                TextMode mode = c.getSalesOrderIdMode() == null ? TextMode.STARTS_WITH : c.getSalesOrderIdMode();
                if (mode == TextMode.EQUALS) {
                    try {
                        ps.add(cb.equal(root.get("salesOrder").get("id"), Long.parseLong(soStr)));
                    } catch (NumberFormatException ignored) {
                        ps.add(cb.disjunction());
                    }
                } else {
                    Expression<String> soText = root.get("salesOrder").<Long>get("id").as(String.class);
                    String pattern = (mode == TextMode.STARTS_WITH) ? soStr + "%" : "%" + soStr + "%";
                    ps.add(cb.like(soText, pattern));
                }
            }

            if (c.getCustomer() != null && !c.getCustomer().isBlank()) {
                String v = c.getCustomer().trim().toLowerCase();
                Expression<String> customerName = cb.lower(root.get("salesOrder").get("customer").get("name"));
                switch (c.getCustomerMode() == null ? TextMode.CONTAINS : c.getCustomerMode()) {
                    case EQUALS      -> ps.add(cb.equal(customerName, v));
                    case STARTS_WITH -> ps.add(cb.like(customerName, v + "%"));
                    case CONTAINS    -> ps.add(cb.like(customerName, "%" + v + "%"));
                }
            }

            if (c.getDateFrom() != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("date"), c.getDateFrom()));
            }
            if (c.getDateTo() != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("date"), c.getDateTo()));
            }
            if (c.getStatus() != null) {
                ps.add(cb.equal(root.get("status"), c.getStatus()));
            }

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(Predicate[]::new));
        };
    }

    /**
     * Simulates the picking process to fulfill a sales order
     * Validates that qty picked on supplied picking job dto is available at the selected bin
     * if not, it will create another picking job line for the backorder 
     */
    @Override
    @Transactional
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

    @Override
    public long countPending() {
        return pickingJobRepository.countByStatus(PjStatus.PENDING);
    }

}
